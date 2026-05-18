package com.ailtonmartins.transactionservice.application.command;

import java.util.UUID;

public record FailTransactionCommand(
        UUID transactionId,
        String failureReason
) {
}
