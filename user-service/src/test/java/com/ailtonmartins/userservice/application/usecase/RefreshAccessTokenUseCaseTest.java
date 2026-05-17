package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.command.RefreshTokenCommand;
import com.ailtonmartins.userservice.application.port.AccessTokenProvider;
import com.ailtonmartins.userservice.application.result.AuthResult;
import com.ailtonmartins.userservice.domain.exception.RefreshTokenException;
import com.ailtonmartins.userservice.domain.model.RefreshToken;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;
import com.ailtonmartins.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshAccessTokenUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @Test
    void deveGerarNovoAccessTokenQuandoRefreshTokenForValido() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        RefreshToken refreshToken = new RefreshToken(
                user.getId(),
                "refresh-token",
                LocalDateTime.now().plusDays(7)
        );

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(refreshToken));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accessTokenProvider.generate(user)).thenReturn("novo-access-token");

        AuthResult result = refreshAccessTokenUseCase.execute(new RefreshTokenCommand("refresh-token"));

        assertThat(result.userId()).isEqualTo(user.getId());
        assertThat(result.accessToken()).isEqualTo("novo-access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.refreshTokenExpiresAt()).isEqualTo(refreshToken.getExpiresAt());
    }

    @Test
    void deveLancarExcecaoQuandoRefreshTokenNaoExistir() {
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshAccessTokenUseCase.execute(new RefreshTokenCommand("refresh-token")))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token invalido");
    }

    @Test
    void deveLancarExcecaoQuandoRefreshTokenEstiverExpirado() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        RefreshToken refreshToken = new RefreshToken(
                user.getId(),
                "refresh-token",
                LocalDateTime.now().minusDays(1)
        );

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshAccessTokenUseCase.execute(new RefreshTokenCommand("refresh-token")))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token expirado");
    }

    @Test
    void deveLancarExcecaoQuandoRefreshTokenEstiverRevogado() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        RefreshToken refreshToken = new RefreshToken(
                user.getId(),
                "refresh-token",
                LocalDateTime.now().plusDays(7)
        );
        refreshToken.revoke();

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshAccessTokenUseCase.execute(new RefreshTokenCommand("refresh-token")))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token revogado");
    }
}
