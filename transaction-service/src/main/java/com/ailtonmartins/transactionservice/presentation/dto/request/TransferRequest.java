package com.ailtonmartins.transactionservice.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados para solicitacao de transferencia")
public record TransferRequest(
        @Schema(description = "Conta de origem", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "conta de origem e obrigatoria")
        UUID sourceAccountId,

        @Schema(description = "Conta de destino", example = "660e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "conta de destino e obrigatoria")
        UUID destinationAccountId,

        @Schema(description = "Valor da transferencia", example = "100.00")
        @NotNull(message = "valor e obrigatorio")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal amount
) {
}
