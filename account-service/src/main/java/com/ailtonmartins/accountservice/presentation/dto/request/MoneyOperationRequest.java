package com.ailtonmartins.accountservice.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados para operacoes financeiras em conta")
public record MoneyOperationRequest(
        @Schema(description = "Valor da operacao", example = "100.00")
        @NotNull(message = "valor e obrigatorio")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal amount
) {
}
