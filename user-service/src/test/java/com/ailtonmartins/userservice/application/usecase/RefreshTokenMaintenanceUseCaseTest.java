package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenMaintenanceUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void deveRevogarRefreshTokensPorUsuario() {
        UUID userId = UUID.randomUUID();
        RevokeRefreshTokensByUserUseCase useCase = new RevokeRefreshTokensByUserUseCase(refreshTokenRepository);

        useCase.execute(userId);

        verify(refreshTokenRepository).revokeByUserId(userId);
    }

    @Test
    void deveRemoverRefreshTokensExpirados() {
        DeleteExpiredRefreshTokensUseCase useCase = new DeleteExpiredRefreshTokensUseCase(refreshTokenRepository);

        useCase.execute();

        verify(refreshTokenRepository).deleteExpiredTokens();
    }
}
