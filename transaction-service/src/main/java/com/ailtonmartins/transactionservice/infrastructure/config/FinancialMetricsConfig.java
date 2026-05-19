package com.ailtonmartins.transactionservice.infrastructure.config;

import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.infrastructure.outbox.JpaOutboxEventRepository;
import com.ailtonmartins.transactionservice.infrastructure.persistence.repository.JpaTransactionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinancialMetricsConfig {

    public FinancialMetricsConfig(
            MeterRegistry meterRegistry,
            JpaTransactionRepository transactionRepository,
            JpaOutboxEventRepository outboxEventRepository
    ) {
        meterRegistry.gauge(
                "financial.transactions.pending",
                transactionRepository,
                repository -> repository.countByStatus(TransactionStatus.PENDING)
        );
        meterRegistry.gauge(
                "financial.transactions.failed",
                transactionRepository,
                repository -> repository.countByStatus(TransactionStatus.FAILED)
        );
        meterRegistry.gauge(
                "financial.outbox.pending",
                outboxEventRepository,
                repository -> repository.countByStatus("PENDING")
        );
        meterRegistry.gauge(
                "financial.outbox.failed",
                outboxEventRepository,
                repository -> repository.countByStatus("FAILED")
        );
    }
}
