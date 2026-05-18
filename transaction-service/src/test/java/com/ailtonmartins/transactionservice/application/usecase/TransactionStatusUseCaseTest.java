package com.ailtonmartins.transactionservice.application.usecase;

import com.ailtonmartins.transactionservice.application.command.FailTransactionCommand;
import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;
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
class TransactionStatusUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CompleteTransactionUseCase completeTransactionUseCase;

    @InjectMocks
    private FailTransactionUseCase failTransactionUseCase;

    @Test
    void deveConcluirTransacao() {
        Transaction transaction = transaction();
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResult result = completeTransactionUseCase.execute(transaction.getId());

        assertThat(result.status()).isEqualTo(TransactionStatus.COMPLETED);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void deveFalharTransacao() {
        Transaction transaction = transaction();
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResult result = failTransactionUseCase.execute(
                new FailTransactionCommand(transaction.getId(), "Saldo insuficiente")
        );

        assertThat(result.status()).isEqualTo(TransactionStatus.FAILED);
        assertThat(result.failureReason()).isEqualTo("Saldo insuficiente");
        verify(transactionRepository).save(transaction);
    }

    @Test
    void deveLancarExcecaoQuandoTransacaoNaoExistir() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> completeTransactionUseCase.execute(transactionId))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Transacao nao encontrada para o id: " + transactionId);

        verify(transactionRepository, never()).save(any());
    }

    private static Transaction transaction() {
        return new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );
    }
}
