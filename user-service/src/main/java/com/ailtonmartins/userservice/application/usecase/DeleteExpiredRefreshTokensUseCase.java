package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;

import java.util.Objects;

public class DeleteExpiredRefreshTokensUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public DeleteExpiredRefreshTokensUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = Objects.requireNonNull(
                refreshTokenRepository,
                "refreshTokenRepository nao pode ser nulo"
        );
    }

    public void execute() {
        refreshTokenRepository.deleteExpiredTokens();
    }
}
