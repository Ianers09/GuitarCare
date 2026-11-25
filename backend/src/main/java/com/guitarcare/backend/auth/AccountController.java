package com.guitarcare.backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private static final String SESSION_USER_ID="GUITARCARE_USER_ID";
    private final AppUserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final SecureRandom random=new SecureRandom();

    public AccountController(AppUserRepository users, PasswordResetTokenRepository tokens,
            PasswordEncoder encoder, JavaMailSender mailSender) {
        this.users=users; this.tokens=tokens; this.encoder=encoder; this.mailSender=mailSender;
    }

    public record ProfileRequest(@NotBlank @Size(max=120) String fullName) {}
    public record ChangePasswordRequest(@NotBlank String currentPassword,
            @NotBlank @Size(min=8,max=72) String newPassword,
            @NotBlank String confirmPassword) {}
    public record ForgotRequest(@NotBlank @jakarta.validation.constraints.Email String email) {}
    public record ResetRequest(@NotBlank String token, @NotBlank @Size(min=8,max=72) String newPassword,
            @NotBlank String confirmPassword) {}
    public record Message(String message) {}

    private AppUser current(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session==null || session.getAttribute(SESSION_USER_ID)==null) return null;
        try { return users.findById(UUID.fromString((String)session.getAttribute(SESSION_USER_ID))).orElse(null); }
        catch(IllegalArgumentException ex){ return null; }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        AppUser user=current(request);
        return user==null ? ResponseEntity.status(401).body(new Message("Please sign in again.")) :
                ResponseEntity.ok(AuthDtos.UserResponse.from(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileRequest body, HttpServletRequest request) {
        AppUser user=current(request);
        if(user==null) return ResponseEntity.status(401).body(new Message("Please sign in again."));
        String name=body.fullName().trim();
        if(name.length()<2) return ResponseEntity.badRequest().body(new Message("Name must contain at least 2 characters."));
        user.updateFullName(name);
        users.save(user);
        return ResponseEntity.ok(AuthDtos.UserResponse.from(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest body, HttpServletRequest request) {
        AppUser user=current(request);
        if(user==null) return ResponseEntity.status(401).body(new Message("Please sign in again."));
        if(!encoder.matches(body.currentPassword(), user.getPasswordHash()))
            return ResponseEntity.badRequest().body(new Message("Your current password is incorrect."));
        if(!body.newPassword().equals(body.confirmPassword()))
            return ResponseEntity.badRequest().body(new Message("New password and confirmation do not match."));
        if(encoder.matches(body.newPassword(), user.getPasswordHash()))
            return ResponseEntity.badRequest().body(new Message("Choose a password different from your current password."));
        user.updatePasswordHash(encoder.encode(body.newPassword()));
        users.save(user);
        HttpSession session=request.getSession(false);
        if(session!=null) session.invalidate();
        return ResponseEntity.ok(new Message("Password changed successfully. Please sign in with your new password."));
    }

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotRequest body) {
        String email=body.email().trim().toLowerCase(Locale.ROOT);
        AppUser user=users.findByEmail(email).orElse(null);
        // Always return the same response to avoid revealing whether an email is registered.
        if(user!=null) {
            try {
                byte[] bytes=new byte[32]; random.nextBytes(bytes);
                String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                String hash=sha256(raw);
                tokens.deleteByUserId(user.getId());
                tokens.save(new PasswordResetToken(user.getId(),hash,Instant.now().plusSeconds(30*60)));
                String url=System.getenv().getOrDefault("APP_PUBLIC_URL","http://localhost:5174")+"/reset-password?token="+raw;
                SimpleMailMessage mail=new SimpleMailMessage();
                mail.setTo(user.getEmail());
                mail.setSubject("GuitarCare password reset");
                mail.setText("Hello "+user.getFullName()+",\n\nUse this link to reset your GuitarCare password. It expires in 30 minutes and can only be used once:\n\n"+url+"\n\nIf you did not request this, ignore this email.");
                mailSender.send(mail);
            } catch(Exception ex) {
                // Do not reveal account existence or mail infrastructure details to callers.
                return ResponseEntity.ok(new Message("If an account exists for that email, password reset instructions will be sent."));
            }
        }
        return ResponseEntity.ok(new Message("If an account exists for that email, password reset instructions will be sent."));
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetRequest body) {
        if(!body.newPassword().equals(body.confirmPassword()))
            return ResponseEntity.badRequest().body(new Message("New password and confirmation do not match."));
        PasswordResetToken token=tokens.findByTokenHash(sha256(body.token())).orElse(null);
        if(token==null || token.getUsedAt()!=null || token.getExpiresAt().isBefore(Instant.now()))
            return ResponseEntity.badRequest().body(new Message("This password reset link is invalid or has expired. Request a new one."));
        AppUser user=users.findById(token.getUserId()).orElse(null);
        if(user==null) return ResponseEntity.badRequest().body(new Message("This password reset link is invalid or has expired."));
        user.updatePasswordHash(encoder.encode(body.newPassword()));
        users.save(user);
        token.markUsed();
        tokens.save(token);
        return ResponseEntity.ok(new Message("Password reset successfully. You can now sign in."));
    }

    private static String sha256(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch(Exception ex){ throw new IllegalStateException("Unable to process reset token.",ex); }
    }
}
