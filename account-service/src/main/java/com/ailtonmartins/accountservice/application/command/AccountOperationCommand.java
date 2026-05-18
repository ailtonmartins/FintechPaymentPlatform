package com.ailtonmartins.accountservice.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountOperationCommand(UUID accountId, BigDecimal amount) {
}
