package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.command.CreateAccountCommand;
import com.ailtonmartins.accountservice.application.port.AccountNumberGenerator;
import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.domain.exception.AccountAlreadyExistsException;
import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    @Test
    void deveCriarContaComSaldoZerado() {
        UUID userId = UUID.randomUUID();
        CreateAccountCommand command = new CreateAccountCommand(userId);

        when(accountRepository.existsByUserId(userId)).thenReturn(false);
        when(accountNumberGenerator.generate()).thenReturn("12345678");
        when(accountRepository.existsByAccountNumber("12345678")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResult result = createAccountUseCase.execute(command);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());

        Account savedAccount = accountCaptor.getValue();
        assertThat(savedAccount.getUserId()).isEqualTo(userId);
        assertThat(savedAccount.getAccountNumber()).isEqualTo("12345678");
        assertThat(savedAccount.getBalance()).isZero();
        assertThat(savedAccount.isActive()).isTrue();
        assertThat(result.id()).isEqualTo(savedAccount.getId());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioJaPossuirConta() {
        UUID userId = UUID.randomUUID();
        CreateAccountCommand command = new CreateAccountCommand(userId);

        when(accountRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> createAccountUseCase.execute(command))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessage("Ja existe uma conta cadastrada para o usuario: " + userId);

        verify(accountNumberGenerator, never()).generate();
        verify(accountRepository, never()).save(any());
    }
}
