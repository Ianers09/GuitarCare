package com.guitarcare.backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.UUID;

import static com.guitarcare.backend.auth.AuthDtos.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String SESSION_USER_ID = "GUITARCARE_USER_ID";

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("An account with this email already exists."));
        }

        AppUser user = users.save(new AppUser(
                request.fullName().trim(), email, passwordEncoder.encode(request.password())
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        AppUser user = users.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid email or password."));
        }

        HttpSession oldSession = servletRequest.getSession(false);
        if (oldSession != null) oldSession.invalidate();
        HttpSession session = servletRequest.getSession(true);
        session.setAttribute(SESSION_USER_ID, user.getId().toString());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Not signed in."));
        }
        try {
            UUID userId = UUID.fromString((String) session.getAttribute(SESSION_USER_ID));
            return users.findById(userId)
                    .<ResponseEntity<?>>map(user -> ResponseEntity.ok(UserResponse.from(user)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new MessageResponse("Session user no longer exists.")));
        } catch (IllegalArgumentException ex) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid session."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.ok(new MessageResponse("Signed out successfully."));
    }
}
