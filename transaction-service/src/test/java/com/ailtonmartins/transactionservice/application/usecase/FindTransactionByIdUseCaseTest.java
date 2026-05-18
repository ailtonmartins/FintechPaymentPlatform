package com.ailtonmartins.transactionservice.application.usecase;

import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTransactionByIdUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FindTransactionByIdUseCase findTransactionByIdUseCase;

    @Test
    void deveBuscarTransacaoPorId() {
        Transaction transaction = transaction();
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        TransactionResult result = findTransactionByIdUseCase.execute(transaction.getId());

        assertThat(result.id()).isEqualTo(transaction.getId());
        assertThat(result.amount()).isEqualByComparingTo("100.00");
    }

    @Test
    void deveLancarExcecaoQuandoTransacaoNaoExistir() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findTransactionByIdUseCase.execute(transactionId))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Transacao nao encontrada para o id: " + transactionId);
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
