package com.ailtonmartins.transactionservice.presentation.dto.response;

import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.domain.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados da transacao")
public record TransactionResponse(
        @Schema(description = "Identificador da transacao", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Usuario que solicitou a transacao", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID requesterUserId,

        @Schema(description = "Conta de origem", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sourceAccountId,

        @Schema(description = "Conta de destino", example = "660e8400-e29b-41d4-a716-446655440000")
        UUID destinationAccountId,

        @Schema(description = "Valor da transacao", example = "100.00")
        BigDecimal amount,

        @Schema(description = "Tipo da transacao", example = "TRANSFER")
        TransactionType type,

        @Schema(description = "Status da transacao", example = "PENDING")
        TransactionStatus status,

        @Schema(description = "Motivo de falha, quando houver", example = "Saldo insuficiente")
        String failureReason,

        @Schema(description = "Data de criacao da transacao")
        LocalDateTime createdAt,

        @Schema(description = "Data da ultima atualizacao da transacao")
        LocalDateTime updatedAt
) {

    public static TransactionResponse from(TransactionResult result) {
        return new TransactionResponse(
                result.id(),
                result.requesterUserId(),
                result.sourceAccountId(),
                result.destinationAccountId(),
                result.amount(),
                result.type(),
                result.status(),
                result.failureReason(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
