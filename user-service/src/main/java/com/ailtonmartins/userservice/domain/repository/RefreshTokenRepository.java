package com.ailtonmartins.userservice.domain.repository;

import com.ailtonmartins.userservice.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeByUserId(UUID userId);

    void deleteExpiredTokens();
}
