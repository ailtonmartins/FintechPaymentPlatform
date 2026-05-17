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

import java.time.LocalDateTime;
import java.util.Objects;

public class LoginUseCase {

    private static final long REFRESH_TOKEN_DAYS_TO_EXPIRE = 7;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordHasher;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenGenerator refreshTokenGenerator;

    public LoginUseCase(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider,
            RefreshTokenGenerator refreshTokenGenerator
    ) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository nao pode ser nulo");
        this.refreshTokenRepository = Objects.requireNonNull(
                refreshTokenRepository,
                "refreshTokenRepository nao pode ser nulo"
        );
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "passwordHasher nao pode ser nulo");
        this.accessTokenProvider = Objects.requireNonNull(accessTokenProvider, "accessTokenProvider nao pode ser nulo");
        this.refreshTokenGenerator = Objects.requireNonNull(
                refreshTokenGenerator,
                "refreshTokenGenerator nao pode ser nulo"
        );
    }

    public AuthResult execute(LoginCommand command) {
        Objects.requireNonNull(command, "command nao pode ser nulo");

        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive() || !passwordHasher.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        RefreshToken refreshToken = createRefreshToken(user);

        return new AuthResult(
                user.getId(),
                accessTokenProvider.generate(user),
                refreshToken.getToken(),
                refreshToken.getExpiresAt()
        );
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken(
                user.getId(),
                refreshTokenGenerator.generate(),
                LocalDateTime.now().plusDays(REFRESH_TOKEN_DAYS_TO_EXPIRE)
        );

        return refreshTokenRepository.save(refreshToken);
    }
}
