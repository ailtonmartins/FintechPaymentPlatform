package com.ailtonmartins.userservice.presentation.dto.response;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.domain.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados publicos do usuario")
public record UserResponse(
        @Schema(description = "Identificador do usuario", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome completo do usuario", example = "Ailton Martins")
        String name,

        @Schema(description = "E-mail do usuario", example = "ailton@email.com")
        String email,

        @Schema(description = "Roles do usuario", example = "[\"USER\"]")
        Set<Role> roles,

        @Schema(description = "Indica se o usuario esta ativo", example = "true")
        boolean active,

        @Schema(description = "Data e hora de criacao")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da ultima atualizacao")
        LocalDateTime updatedAt
) {

    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.id(),
                result.name(),
                result.email(),
                result.roles(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
