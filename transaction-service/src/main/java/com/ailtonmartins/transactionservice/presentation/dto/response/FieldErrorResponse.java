package com.ailtonmartins.transactionservice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Erro de validacao por campo")
public record FieldErrorResponse(
        @Schema(description = "Nome do campo", example = "amount")
        String field,

        @Schema(description = "Mensagem de validacao", example = "valor deve ser maior que zero")
        String message
) {
}
