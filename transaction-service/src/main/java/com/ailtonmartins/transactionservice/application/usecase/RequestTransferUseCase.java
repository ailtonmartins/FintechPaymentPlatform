package com.ailtonmartins.transactionservice.application.usecase;

import com.ailtonmartins.transactionservice.application.command.RequestTransferCommand;
import com.ailtonmartins.transactionservice.application.port.TransactionEventPublisher;
import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;

public class RequestTransferUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionEventPublisher transactionEventPublisher;

    public RequestTransferUseCase(
            TransactionRepository transactionRepository,
            TransactionEventPublisher transactionEventPublisher
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionEventPublisher = transactionEventPublisher;
    }

    @Transactional
    public TransactionResult execute(RequestTransferCommand command) {
        Transaction transaction = new Transaction(
                command.requesterUserId(),
                command.sourceAccountId(),
                command.destinationAccountId(),
                command.amount()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionEventPublisher.publishTransferRequested(savedTransaction);

        return TransactionResult.from(savedTransaction);
    }
}
