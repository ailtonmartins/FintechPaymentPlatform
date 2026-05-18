package com.ailtonmartins.accountservice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta padrao de erro")
public record ErrorResponse(
        @Schema(description = "Data e hora do erro")
        LocalDateTime timestamp,

        @Schema(description = "Codigo HTTP", example = "400")
        int status,

        @Schema(description = "Descricao HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensagem do erro", example = "Requisicao invalida")
        String message,

        @Schema(description = "Caminho da requisicao", example = "/api/v1/accounts")
        String path,

        @Schema(description = "Erros por campo")
        List<FieldErrorResponse> fields
) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse withFields(
            int status,
            String error,
            String message,
            String path,
            List<FieldErrorResponse> fields
    ) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, fields);
    }
}
