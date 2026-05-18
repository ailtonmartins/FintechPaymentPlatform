package com.ailtonmartins.accountservice.presentation.dto.response;

import com.ailtonmartins.accountservice.application.result.AccountResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados publicos da conta")
public record AccountResponse(
        @Schema(description = "Identificador da conta", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identificador do usuario dono da conta", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "Numero da conta", example = "12345678")
        String accountNumber,

        @Schema(description = "Saldo atual", example = "250.00")
        BigDecimal balance,

        @Schema(description = "Indica se a conta esta ativa", example = "true")
        boolean active,

        @Schema(description = "Data de criacao da conta")
        LocalDateTime createdAt,

        @Schema(description = "Data da ultima atualizacao da conta")
        LocalDateTime updatedAt
) {

    public static AccountResponse from(AccountResult result) {
        return new AccountResponse(
                result.id(),
                result.userId(),
                result.accountNumber(),
                result.balance(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
