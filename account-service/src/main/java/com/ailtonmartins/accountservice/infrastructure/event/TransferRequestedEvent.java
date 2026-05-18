package com.ailtonmartins.accountservice.infrastructure.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestedEvent(
        UUID transactionId,
        UUID requesterUserId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String type,
        String status
) {
}
