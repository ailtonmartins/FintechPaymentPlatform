package com.ailtonmartins.userservice.infrastructure.config;

import com.ailtonmartins.userservice.application.port.AccessTokenProvider;
import com.ailtonmartins.userservice.application.port.PasswordHasher;
import com.ailtonmartins.userservice.application.port.RefreshTokenGenerator;
import com.ailtonmartins.userservice.application.usecase.DeleteExpiredRefreshTokensUseCase;
import com.ailtonmartins.userservice.application.usecase.FindUserByEmailUseCase;
import com.ailtonmartins.userservice.application.usecase.FindUserByIdUseCase;
import com.ailtonmartins.userservice.application.usecase.LoginUseCase;
import com.ailtonmartins.userservice.application.usecase.RefreshAccessTokenUseCase;
import com.ailtonmartins.userservice.application.usecase.RegisterUserUseCase;
import com.ailtonmartins.userservice.application.usecase.RevokeRefreshTokensByUserUseCase;
import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;
import com.ailtonmartins.userservice.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        return new RegisterUserUseCase(userRepository, passwordHasher);
    }

    @Bean
    public LoginUseCase loginUseCase(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider,
            RefreshTokenGenerator refreshTokenGenerator
    ) {
        return new LoginUseCase(
                userRepository,
                refreshTokenRepository,
                passwordHasher,
                accessTokenProvider,
                refreshTokenGenerator
        );
    }

    @Bean
    public RefreshAccessTokenUseCase refreshAccessTokenUseCase(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            AccessTokenProvider accessTokenProvider
    ) {
        return new RefreshAccessTokenUseCase(userRepository, refreshTokenRepository, accessTokenProvider);
    }

    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserRepository userRepository) {
        return new FindUserByIdUseCase(userRepository);
    }

    @Bean
    public FindUserByEmailUseCase findUserByEmailUseCase(UserRepository userRepository) {
        return new FindUserByEmailUseCase(userRepository);
    }

    @Bean
    public RevokeRefreshTokensByUserUseCase revokeRefreshTokensByUserUseCase(
            RefreshTokenRepository refreshTokenRepository
    ) {
        return new RevokeRefreshTokensByUserUseCase(refreshTokenRepository);
    }

    @Bean
    public DeleteExpiredRefreshTokensUseCase deleteExpiredRefreshTokensUseCase(
            RefreshTokenRepository refreshTokenRepository
    ) {
        return new DeleteExpiredRefreshTokensUseCase(refreshTokenRepository);
    }
}
