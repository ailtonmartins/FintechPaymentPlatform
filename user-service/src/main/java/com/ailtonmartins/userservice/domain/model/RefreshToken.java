package com.ailtonmartins.userservice.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {

    private UUID id;
    private UUID userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean revoked;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;

    public RefreshToken(UUID userId, String token, LocalDateTime expiresAt) {
        this(UUID.randomUUID(), userId, token, expiresAt, false, LocalDateTime.now(), null);
    }

    public RefreshToken(
            UUID id,
            UUID userId,
            String token,
            LocalDateTime expiresAt,
            boolean revoked,
            LocalDateTime createdAt,
            LocalDateTime revokedAt
    ) {
        this.id = Objects.requireNonNull(id, "id nao pode ser nulo");
        this.userId = Objects.requireNonNull(userId, "userId nao pode ser nulo");
        this.token = requireText(token, "token");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt nao pode ser nulo");
        this.revoked = revoked;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt nao pode ser nulo");
        this.revokedAt = revokedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nao pode estar em branco");
        }
        return value.trim();
    }
}
