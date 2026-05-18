package com.ailtonmartins.transactionservice.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestTransferCommand(
        UUID requesterUserId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount
) {
}
