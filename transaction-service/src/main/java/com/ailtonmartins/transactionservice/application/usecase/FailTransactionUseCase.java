package com.ailtonmartins.transactionservice.application.usecase;

import com.ailtonmartins.transactionservice.application.command.FailTransactionCommand;
import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;

public class FailTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public FailTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResult execute(FailTransactionCommand command) {
        Transaction transaction = transactionRepository.findById(command.transactionId())
                .orElseThrow(() -> new TransactionNotFoundException(command.transactionId()));

        if (transaction.isFinished()) {
            return TransactionResult.from(transaction);
        }

        transaction.fail(command.failureReason());

        return TransactionResult.from(transactionRepository.save(transaction));
    }
}
