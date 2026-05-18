package com.ailtonmartins.transactionservice.infrastructure.config;

import com.ailtonmartins.transactionservice.application.port.TransactionEventPublisher;
import com.ailtonmartins.transactionservice.application.usecase.CompleteTransactionUseCase;
import com.ailtonmartins.transactionservice.application.usecase.FailTransactionUseCase;
import com.ailtonmartins.transactionservice.application.usecase.FindTransactionByIdUseCase;
import com.ailtonmartins.transactionservice.application.usecase.RequestTransferUseCase;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RequestTransferUseCase requestTransferUseCase(
            TransactionRepository transactionRepository,
            TransactionEventPublisher transactionEventPublisher
    ) {
        return new RequestTransferUseCase(transactionRepository, transactionEventPublisher);
    }

    @Bean
    public FindTransactionByIdUseCase findTransactionByIdUseCase(TransactionRepository transactionRepository) {
        return new FindTransactionByIdUseCase(transactionRepository);
    }

    @Bean
    public CompleteTransactionUseCase completeTransactionUseCase(TransactionRepository transactionRepository) {
        return new CompleteTransactionUseCase(transactionRepository);
    }

    @Bean
    public FailTransactionUseCase failTransactionUseCase(TransactionRepository transactionRepository) {
        return new FailTransactionUseCase(transactionRepository);
    }
}
