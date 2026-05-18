package com.ailtonmartins.transactionservice.application.usecase;

import com.ailtonmartins.transactionservice.application.command.RequestTransferCommand;
import com.ailtonmartins.transactionservice.application.port.TransactionEventPublisher;
import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTransferUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionEventPublisher transactionEventPublisher;

    @InjectMocks
    private RequestTransferUseCase requestTransferUseCase;

    @Test
    void deveCriarTransacaoPendenteEPublicarEvento() {
        UUID requesterUserId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        RequestTransferCommand command = new RequestTransferCommand(
                requesterUserId,
                sourceAccountId,
                destinationAccountId,
                new BigDecimal("100.00")
        );

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResult result = requestTransferUseCase.execute(command);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        verify(transactionEventPublisher).publishTransferRequested(transactionCaptor.getValue());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertThat(savedTransaction.getRequesterUserId()).isEqualTo(requesterUserId);
        assertThat(savedTransaction.getSourceAccountId()).isEqualTo(sourceAccountId);
        assertThat(savedTransaction.getDestinationAccountId()).isEqualTo(destinationAccountId);
        assertThat(savedTransaction.getAmount()).isEqualByComparingTo("100.00");
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(result.id()).isEqualTo(savedTransaction.getId());
        assertThat(result.status()).isEqualTo(TransactionStatus.PENDING);
    }
}
