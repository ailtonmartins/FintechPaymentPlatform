package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.command.AccountOperationCommand;
import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.domain.exception.AccountNotFoundException;
import com.ailtonmartins.accountservice.domain.exception.InsufficientBalanceException;
import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountOperationUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreditAccountUseCase creditAccountUseCase;

    @InjectMocks
    private DebitAccountUseCase debitAccountUseCase;

    @Test
    void deveCreditarConta() {
        Account account = new Account(UUID.randomUUID(), UUID.randomUUID(), "12345678", BigDecimal.ZERO, true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResult result = creditAccountUseCase.execute(new AccountOperationCommand(account.getId(), new BigDecimal("50.00")));

        assertThat(result.balance()).isEqualByComparingTo("50.00");
        verify(accountRepository).save(account);
    }

    @Test
    void deveDebitarConta() {
        Account account = new Account(UUID.randomUUID(), UUID.randomUUID(), "12345678", new BigDecimal("80.00"), true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResult result = debitAccountUseCase.execute(new AccountOperationCommand(account.getId(), new BigDecimal("30.00")));

        assertThat(result.balance()).isEqualByComparingTo("50.00");
        verify(accountRepository).save(account);
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExistir() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditAccountUseCase.execute(new AccountOperationCommand(accountId, new BigDecimal("10.00"))))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Conta nao encontrada para o id: " + accountId);

        verify(accountRepository, never()).save(any());
    }

    @Test
    void naoDeveSalvarQuandoSaldoForInsuficiente() {
        Account account = new Account(UUID.randomUUID(), UUID.randomUUID(), "12345678", BigDecimal.ZERO, true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> debitAccountUseCase.execute(new AccountOperationCommand(account.getId(), new BigDecimal("10.00"))))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo insuficiente");

        verify(accountRepository, never()).save(any());
    }
}
