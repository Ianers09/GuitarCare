package com.guitarcare.backend.auth;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens", indexes = @Index(name="idx_reset_token_hash", columnList="token_hash", unique=true))
public class PasswordResetToken {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="token_hash", nullable=false, unique=true, length=64) private String tokenHash;
    @Column(name="expires_at", nullable=false) private Instant expiresAt;
    @Column(name="used_at") private Instant usedAt;
    protected PasswordResetToken() {}
    public PasswordResetToken(UUID userId, String tokenHash, Instant expiresAt) { this.userId=userId; this.tokenHash=tokenHash; this.expiresAt=expiresAt; }
    public UUID getUserId(){return userId;}
    public String getTokenHash(){return tokenHash;}
    public Instant getExpiresAt(){return expiresAt;}
    public Instant getUsedAt(){return usedAt;}
    public void markUsed(){this.usedAt=Instant.now();}
}
