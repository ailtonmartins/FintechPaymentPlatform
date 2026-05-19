package com.ailtonmartins.accountservice.infrastructure.config;

import com.ailtonmartins.accountservice.infrastructure.outbox.JpaOutboxEventRepository;
import com.ailtonmartins.accountservice.infrastructure.persistence.repository.JpaProcessedTransferEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinancialMetricsConfig {

    public FinancialMetricsConfig(
            MeterRegistry meterRegistry,
            JpaProcessedTransferEventRepository processedTransferEventRepository,
            JpaOutboxEventRepository outboxEventRepository
    ) {
        meterRegistry.gauge(
                "financial.transfers.completed",
                processedTransferEventRepository,
                repository -> repository.countByStatus("COMPLETED")
        );
        meterRegistry.gauge(
                "financial.transfers.failed",
                processedTransferEventRepository,
                repository -> repository.countByStatus("FAILED")
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
