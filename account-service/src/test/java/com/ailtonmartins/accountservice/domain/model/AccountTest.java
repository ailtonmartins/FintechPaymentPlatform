package com.ailtonmartins.accountservice.domain.model;

import com.ailtonmartins.accountservice.domain.exception.InactiveAccountException;
import com.ailtonmartins.accountservice.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    void deveCreditarValorNaConta() {
        Account account = new Account(UUID.randomUUID(), "12345678");

        account.credit(new BigDecimal("100.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void deveDebitarValorQuandoHouverSaldo() {
        Account account = new Account(UUID.randomUUID(), "12345678");
        account.credit(new BigDecimal("100.00"));

        account.debit(new BigDecimal("40.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void deveLancarExcecaoQuandoSaldoForInsuficiente() {
        Account account = new Account(UUID.randomUUID(), "12345678");

        assertThatThrownBy(() -> account.debit(new BigDecimal("10.00")))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo insuficiente");
    }

    @Test
    void deveLancarExcecaoQuandoContaEstiverInativa() {
        Account account = new Account(UUID.randomUUID(), "12345678");
        account.deactivate();

        assertThatThrownBy(() -> account.credit(new BigDecimal("10.00")))
                .isInstanceOf(InactiveAccountException.class)
                .hasMessage("Conta inativa");
    }
}
