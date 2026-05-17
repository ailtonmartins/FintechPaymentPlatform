package com.ailtonmartins.userservice.presentation.controller;

import com.ailtonmartins.userservice.application.result.AuthResult;
import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.LoginUseCase;
import com.ailtonmartins.userservice.application.usecase.RefreshAccessTokenUseCase;
import com.ailtonmartins.userservice.application.usecase.RegisterUserUseCase;
import com.ailtonmartins.userservice.presentation.dto.request.LoginRequest;
import com.ailtonmartins.userservice.presentation.dto.request.RefreshTokenRequest;
import com.ailtonmartins.userservice.presentation.dto.request.RegisterRequest;
import com.ailtonmartins.userservice.presentation.dto.response.AuthResponse;
import com.ailtonmartins.userservice.presentation.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUseCase loginUseCase,
            RefreshAccessTokenUseCase refreshAccessTokenUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResult result = registerUserUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginUseCase.execute(request.toCommand());
        return ResponseEntity.ok(AuthResponse.from(result));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResult result = refreshAccessTokenUseCase.execute(request.toCommand());
        return ResponseEntity.ok(AuthResponse.from(result));
    }
}
