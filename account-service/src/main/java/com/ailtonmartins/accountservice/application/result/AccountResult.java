package com.ailtonmartins.accountservice.application.result;

import com.ailtonmartins.accountservice.domain.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResult(
        UUID id,
        UUID userId,
        String accountNumber,
        BigDecimal balance,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static AccountResult from(Account account) {
        return new AccountResult(
                account.getId(),
                account.getUserId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.isActive(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
