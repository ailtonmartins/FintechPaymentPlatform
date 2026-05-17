package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.command.LoginCommand;
import com.ailtonmartins.userservice.application.port.AccessTokenProvider;
import com.ailtonmartins.userservice.application.port.PasswordHasher;
import com.ailtonmartins.userservice.application.port.RefreshTokenGenerator;
import com.ailtonmartins.userservice.application.result.AuthResult;
import com.ailtonmartins.userservice.domain.exception.InvalidCredentialsException;
import com.ailtonmartins.userservice.domain.model.RefreshToken;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;
import com.ailtonmartins.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private RefreshTokenGenerator refreshTokenGenerator;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    void deveAutenticarUsuarioECriarTokens() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        LoginCommand command = new LoginCommand("ailton@email.com", "123456");

        when(userRepository.findByEmail(command.email())).thenReturn(Optional.of(user));
        when(passwordHasher.matches(command.password(), user.getPassword())).thenReturn(true);
        when(refreshTokenGenerator.generate()).thenReturn("refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accessTokenProvider.generate(user)).thenReturn("access-token");

        AuthResult result = loginUseCase.execute(command);

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken savedRefreshToken = refreshTokenCaptor.getValue();
        assertThat(savedRefreshToken.getUserId()).isEqualTo(user.getId());
        assertThat(savedRefreshToken.getToken()).isEqualTo("refresh-token");
        assertThat(savedRefreshToken.isActive()).isTrue();

        assertThat(result.userId()).isEqualTo(user.getId());
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.refreshTokenExpiresAt()).isEqualTo(savedRefreshToken.getExpiresAt());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        LoginCommand command = new LoginCommand("inexistente@email.com", "123456");

        when(userRepository.findByEmail(command.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais invalidas");

        verify(refreshTokenRepository, never()).save(any());
        verify(accessTokenProvider, never()).generate(any());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForInvalida() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        LoginCommand command = new LoginCommand("ailton@email.com", "senha-errada");

        when(userRepository.findByEmail(command.email())).thenReturn(Optional.of(user));
        when(passwordHasher.matches(command.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais invalidas");

        verify(refreshTokenRepository, never()).save(any());
        verify(accessTokenProvider, never()).generate(any());
    }
}
