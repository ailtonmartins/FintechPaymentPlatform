package com.ailtonmartins.userservice.presentation.controller;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.FindUserByIdUseCase;
import com.ailtonmartins.userservice.infrastructure.config.OpenApiConfig;
import com.ailtonmartins.userservice.presentation.dto.response.ErrorResponse;
import com.ailtonmartins.userservice.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me")
@Tag(name = "Perfil", description = "Endpoints do usuario autenticado")
public class MeController {

    private final FindUserByIdUseCase findUserByIdUseCase;

    public MeController(FindUserByIdUseCase findUserByIdUseCase) {
        this.findUserByIdUseCase = findUserByIdUseCase;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar meu usuario", description = "Retorna os dados do usuario autenticado.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado encontrado"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario autenticado nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<UserResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        UserResult result = findUserByIdUseCase.execute(userId);
        return ResponseEntity.ok(UserResponse.from(result));
    }
}
