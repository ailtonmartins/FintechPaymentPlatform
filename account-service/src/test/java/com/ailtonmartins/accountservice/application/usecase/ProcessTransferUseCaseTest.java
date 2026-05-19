package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.port.ProcessedTransferEventRepository;
import com.ailtonmartins.accountservice.application.port.TransferResultPublisher;
import com.ailtonmartins.accountservice.application.result.ProcessTransferResult;
import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessTransferUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ProcessedTransferEventRepository processedTransferEventRepository;

    @Mock
    private TransferResultPublisher transferResultPublisher;

    @InjectMocks
    private ProcessTransferUseCase processTransferUseCase;

    @Test
    void deveProcessarTransferenciaERegistrarEventoConcluido() {
        ProcessTransferCommand command = command();
        Account sourceAccount = account(command.sourceAccountId(), new BigDecimal("100.00"));
        Account destinationAccount = account(command.destinationAccountId(), new BigDecimal("10.00"));

        when(processedTransferEventRepository.findByTransactionId(command.transactionId()))
                .thenReturn(Optional.empty());
        when(accountRepository.findById(command.sourceAccountId()))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(command.destinationAccountId()))
                .thenReturn(Optional.of(destinationAccount));
        when(processedTransferEventRepository.saveCompleted(command))
                .thenReturn(ProcessTransferResult.completed(command, false));

        ProcessTransferResult result = processTransferUseCase.execute(command);

        assertThat(result.completed()).isTrue();
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("75.00");
        assertThat(destinationAccount.getBalance()).isEqualByComparingTo("35.00");
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(destinationAccount);
        verify(processedTransferEventRepository).saveCompleted(command);
        verify(transferResultPublisher).publishCompleted(command);
    }

    @Test
    void naoDeveMovimentarSaldoQuandoTransferenciaJaFoiProcessada() {
        ProcessTransferCommand command = command();
        when(processedTransferEventRepository.findByTransactionId(command.transactionId()))
                .thenReturn(Optional.of(ProcessTransferResult.completed(command, false)));

        ProcessTransferResult result = processTransferUseCase.execute(command);

        assertThat(result.completed()).isTrue();
        assertThat(result.alreadyProcessed()).isTrue();
        verify(accountRepository, never()).findById(command.sourceAccountId());
        verify(accountRepository, never()).save(org.mockito.Mockito.any());
        verify(transferResultPublisher, never()).publishCompleted(command);
    }

    @Test
    void deveRegistrarFalhaDeNegocioSemMovimentarSaldoQuandoSaldoForInsuficiente() {
        ProcessTransferCommand command = command();
        Account sourceAccount = account(command.sourceAccountId(), new BigDecimal("10.00"));
        Account destinationAccount = account(command.destinationAccountId(), new BigDecimal("20.00"));

        when(processedTransferEventRepository.findByTransactionId(command.transactionId()))
                .thenReturn(Optional.empty());
        when(accountRepository.findById(command.sourceAccountId()))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(command.destinationAccountId()))
                .thenReturn(Optional.of(destinationAccount));
        when(processedTransferEventRepository.saveFailed(command, "Saldo insuficiente"))
                .thenReturn(ProcessTransferResult.failed(command, "Saldo insuficiente", false));

        ProcessTransferResult result = processTransferUseCase.execute(command);

        assertThat(result.completed()).isFalse();
        assertThat(result.failureReason()).isEqualTo("Saldo insuficiente");
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("10.00");
        assertThat(destinationAccount.getBalance()).isEqualByComparingTo("20.00");
        verify(accountRepository, never()).save(org.mockito.Mockito.any());
        verify(processedTransferEventRepository).saveFailed(command, "Saldo insuficiente");
        verify(transferResultPublisher).publishFailed(command, "Saldo insuficiente");
    }

    private static ProcessTransferCommand command() {
        return new ProcessTransferCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("25.00")
        );
    }

    private static Account account(UUID id, BigDecimal balance) {
        return new Account(
                id,
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                balance,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
