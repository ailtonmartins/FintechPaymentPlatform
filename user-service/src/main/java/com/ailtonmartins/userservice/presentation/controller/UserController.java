package com.ailtonmartins.userservice.presentation.controller;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.FindUserByEmailUseCase;
import com.ailtonmartins.userservice.application.usecase.FindUserByIdUseCase;
import com.ailtonmartins.userservice.infrastructure.config.OpenApiConfig;
import com.ailtonmartins.userservice.presentation.dto.response.ErrorResponse;
import com.ailtonmartins.userservice.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Endpoints de consulta de usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindUserByEmailUseCase findUserByEmailUseCase;

    public UserController(
            FindUserByIdUseCase findUserByIdUseCase,
            FindUserByEmailUseCase findUserByEmailUseCase
    ) {
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.findUserByEmailUseCase = findUserByEmailUseCase;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por id", description = "Retorna os dados publicos de um usuario pelo identificador.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado para usuarios sem role ADMIN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "Identificador do usuario", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id
    ) {
        UserResult result = findUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserResponse.from(result));
    }

    @GetMapping
    @Operation(summary = "Buscar usuario por e-mail", description = "Retorna os dados publicos de um usuario pelo e-mail.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parametro e-mail ausente ou invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado para usuarios sem role ADMIN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<UserResponse> findByEmail(
            @Parameter(description = "E-mail do usuario", example = "ailton@email.com")
            @RequestParam String email
    ) {
        UserResult result = findUserByEmailUseCase.execute(email);
        return ResponseEntity.ok(UserResponse.from(result));
    }
}
