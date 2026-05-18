package com.ailtonmartins.accountservice.infrastructure.event;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferFailedEvent(
        UUID transactionId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String failureReason,
        LocalDateTime occurredAt
) {

    public static TransferFailedEvent from(ProcessTransferCommand command, String failureReason) {
        return new TransferFailedEvent(
                command.transactionId(),
                command.sourceAccountId(),
                command.destinationAccountId(),
                command.amount(),
                failureReason,
                LocalDateTime.now()
        );
    }
}
