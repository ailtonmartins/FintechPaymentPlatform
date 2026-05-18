package com.ailtonmartins.transactionservice.application.usecase;

import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;

import java.util.UUID;

public class FindTransactionByIdUseCase {

    private final TransactionRepository transactionRepository;

    public FindTransactionByIdUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResult execute(UUID id) {
        return transactionRepository.findById(id)
                .map(TransactionResult::from)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }
}
