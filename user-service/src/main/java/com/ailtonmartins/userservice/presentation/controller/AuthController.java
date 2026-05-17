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
import com.ailtonmartins.userservice.presentation.dto.response.ErrorResponse;
import com.ailtonmartins.userservice.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacao", description = "Endpoints de cadastro, login e renovacao de access token")
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
    @Operation(summary = "Cadastrar usuario", description = "Cria um novo usuario com role USER e senha criptografada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario cadastrado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisicao invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail ja cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResult result = registerUserUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(result));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario", description = "Valida credenciais e retorna access token JWT e refresh token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisicao invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais invalidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginUseCase.execute(request.toCommand());
        return ResponseEntity.ok(AuthResponse.from(result));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Renovar access token", description = "Gera um novo access token usando um refresh token valido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token renovado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisicao invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token invalido, expirado ou revogado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResult result = refreshAccessTokenUseCase.execute(request.toCommand());
        return ResponseEntity.ok(AuthResponse.from(result));
    }
}
