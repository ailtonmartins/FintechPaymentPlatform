package com.ailtonmartins.accountservice.domain.model;

import com.ailtonmartins.accountservice.domain.exception.InactiveAccountException;
import com.ailtonmartins.accountservice.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Account {

    private UUID id;
    private UUID userId;
    private String accountNumber;
    private BigDecimal balance;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Account(UUID userId, String accountNumber) {
        this(UUID.randomUUID(), userId, accountNumber, BigDecimal.ZERO, true, LocalDateTime.now(), LocalDateTime.now());
    }

    public Account(
            UUID id,
            UUID userId,
            String accountNumber,
            BigDecimal balance,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id nao pode ser nulo");
        this.userId = Objects.requireNonNull(userId, "userId nao pode ser nulo");
        this.accountNumber = requireText(accountNumber, "accountNumber");
        this.balance = requireNonNegative(balance, "balance");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt nao pode ser nulo");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt nao pode ser nulo");
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void credit(BigDecimal amount) {
        ensureActive();
        BigDecimal validAmount = requirePositive(amount, "amount");
        this.balance = this.balance.add(validAmount);
        touch();
    }

    public void debit(BigDecimal amount) {
        ensureActive();
        BigDecimal validAmount = requirePositive(amount, "amount");
        if (this.balance.compareTo(validAmount) < 0) {
            throw new InsufficientBalanceException();
        }
        this.balance = this.balance.subtract(validAmount);
        touch();
    }

    public void deactivate() {
        this.active = false;
        touch();
    }

    private void ensureActive() {
        if (!active) {
            throw new InactiveAccountException();
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nao pode estar em branco");
        }
        return value.trim();
    }

    private static BigDecimal requirePositive(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " nao pode ser nulo");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero");
        }
        return value;
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " nao pode ser nulo");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " nao pode ser negativo");
        }
        return value;
    }
}
