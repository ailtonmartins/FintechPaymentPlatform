package com.ailtonmartins.transactionservice.presentation.controller;

import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.infrastructure.config.OpenApiConfig;
import com.ailtonmartins.transactionservice.infrastructure.operations.KafkaDlqMonitor;
import com.ailtonmartins.transactionservice.infrastructure.outbox.JpaOutboxEventRepository;
import com.ailtonmartins.transactionservice.infrastructure.persistence.repository.JpaTransactionRepository;
import com.ailtonmartins.transactionservice.presentation.dto.response.OperationSummaryResponse;
import com.ailtonmartins.transactionservice.presentation.dto.response.OutboxEventResponse;
import com.ailtonmartins.transactionservice.presentation.dto.response.PendingTransactionResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/operations/transactions")
@Tag(name = "Operacional - Transacoes", description = "Endpoints operacionais do fluxo financeiro")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class TransactionOperationsController {

    private final JpaTransactionRepository transactionRepository;
    private final JpaOutboxEventRepository outboxEventRepository;
    private final KafkaDlqMonitor kafkaDlqMonitor;
    private final String transferCompletedDlqTopic;
    private final String transferFailedDlqTopic;

    public TransactionOperationsController(
            JpaTransactionRepository transactionRepository,
            JpaOutboxEventRepository outboxEventRepository,
            KafkaDlqMonitor kafkaDlqMonitor,
            @Value("${app.kafka.topics.transfer-completed}.dlq") String transferCompletedDlqTopic,
            @Value("${app.kafka.topics.transfer-failed}.dlq") String transferFailedDlqTopic
    ) {
        this.transactionRepository = transactionRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaDlqMonitor = kafkaDlqMonitor;
        this.transferCompletedDlqTopic = transferCompletedDlqTopic;
        this.transferFailedDlqTopic = transferFailedDlqTopic;
    }

    @GetMapping("/summary")
    public OperationSummaryResponse summary() {
        return new OperationSummaryResponse(
                transactionRepository.countByStatus(TransactionStatus.PENDING),
                transactionRepository.countByStatus(TransactionStatus.FAILED),
                outboxEventRepository.countByStatus("PENDING"),
                outboxEventRepository.countByStatus("FAILED"),
                Map.of(
                        transferCompletedDlqTopic, kafkaDlqMonitor.countMessages(transferCompletedDlqTopic),
                        transferFailedDlqTopic, kafkaDlqMonitor.countMessages(transferFailedDlqTopic)
                )
        );
    }

    @GetMapping("/pending")
    public List<PendingTransactionResponse> pendingTransactions() {
        return transactionRepository.findTop20ByStatusOrderByCreatedAtAsc(TransactionStatus.PENDING)
                .stream()
                .map(PendingTransactionResponse::from)
                .toList();
    }

    @GetMapping("/outbox/failed")
    public List<OutboxEventResponse> failedOutboxEvents() {
        return outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc("FAILED")
                .stream()
                .map(OutboxEventResponse::from)
                .toList();
    }
}
