package com.ailtonmartins.accountservice.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessTransferCommand(
        UUID transactionId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount
) {
}
