package com.ailtonmartins.accountservice.infrastructure.event;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID transactionId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        LocalDateTime occurredAt
) {

    public static TransferCompletedEvent from(ProcessTransferCommand command) {
        return new TransferCompletedEvent(
                command.transactionId(),
                command.sourceAccountId(),
                command.destinationAccountId(),
                command.amount(),
                LocalDateTime.now()
        );
    }
}
