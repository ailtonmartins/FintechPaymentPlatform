package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;

import java.util.Objects;
import java.util.UUID;

public class RevokeRefreshTokensByUserUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public RevokeRefreshTokensByUserUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = Objects.requireNonNull(
                refreshTokenRepository,
                "refreshTokenRepository nao pode ser nulo"
        );
    }

    public void execute(UUID userId) {
        Objects.requireNonNull(userId, "userId nao pode ser nulo");
        refreshTokenRepository.revokeByUserId(userId);
    }
}
