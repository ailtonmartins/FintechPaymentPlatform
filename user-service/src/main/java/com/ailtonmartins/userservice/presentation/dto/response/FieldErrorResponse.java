package com.ailtonmartins.userservice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Erro de validacao de campo")
public record FieldErrorResponse(
        @Schema(description = "Nome do campo", example = "email")
        String field,

        @Schema(description = "Mensagem de validacao", example = "E-mail deve ser valido")
        String message
) {
}
