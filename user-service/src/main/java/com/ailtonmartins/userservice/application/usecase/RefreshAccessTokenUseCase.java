package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.command.RefreshTokenCommand;
import com.ailtonmartins.userservice.application.port.AccessTokenProvider;
import com.ailtonmartins.userservice.application.result.AuthResult;
import com.ailtonmartins.userservice.domain.exception.InvalidCredentialsException;
import com.ailtonmartins.userservice.domain.exception.RefreshTokenException;
import com.ailtonmartins.userservice.domain.exception.UserNotFoundException;
import com.ailtonmartins.userservice.domain.model.RefreshToken;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;
import com.ailtonmartins.userservice.domain.repository.UserRepository;

import java.util.Objects;

public class RefreshAccessTokenUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenProvider accessTokenProvider;

    public RefreshAccessTokenUseCase(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            AccessTokenProvider accessTokenProvider
    ) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository nao pode ser nulo");
        this.refreshTokenRepository = Objects.requireNonNull(
                refreshTokenRepository,
                "refreshTokenRepository nao pode ser nulo"
        );
        this.accessTokenProvider = Objects.requireNonNull(accessTokenProvider, "accessTokenProvider nao pode ser nulo");
    }

    public AuthResult execute(RefreshTokenCommand command) {
        Objects.requireNonNull(command, "command nao pode ser nulo");

        RefreshToken refreshToken = validate(command.refreshToken());
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(refreshToken.getUserId()));

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        return new AuthResult(
                user.getId(),
                accessTokenProvider.generate(user),
                refreshToken.getToken(),
                refreshToken.getExpiresAt()
        );
    }

    private RefreshToken validate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(RefreshTokenException::invalid);

        if (refreshToken.isRevoked()) {
            throw RefreshTokenException.revoked();
        }

        if (refreshToken.isExpired()) {
            throw RefreshTokenException.expired();
        }

        return refreshToken;
    }
}
