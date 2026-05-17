package com.ailtonmartins.userservice.infrastructure.security;

import com.ailtonmartins.userservice.application.port.AccessTokenProvider;
import com.ailtonmartins.userservice.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAccessTokenProvider implements AccessTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtAccessTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(sha256(secret));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generate(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(Enum::name).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public JwtUserClaims validateAndExtract(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);
        List<String> roles = extractRoles(claims);

        return new JwtUserClaims(userId, email, roles);
    }

    private static List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof Collection<?> collection) {
            return collection.stream()
                    .map(String::valueOf)
                    .toList();
        }

        return List.of();
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Nao foi possivel inicializar o algoritmo SHA-256", exception);
        }
    }

    public record JwtUserClaims(
            UUID userId,
            String email,
            List<String> roles
    ) {
    }
}
