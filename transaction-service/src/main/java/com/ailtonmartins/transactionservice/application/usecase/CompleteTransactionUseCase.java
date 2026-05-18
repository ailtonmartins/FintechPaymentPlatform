package com.ailtonmartins.transactionservice.application.usecase;

import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;

import java.util.UUID;

public class CompleteTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public CompleteTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResult execute(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        transaction.complete();

        return TransactionResult.from(transactionRepository.save(transaction));
    }
}
