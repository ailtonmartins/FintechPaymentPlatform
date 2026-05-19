package com.ailtonmartins.accountservice.presentation.controller;

import com.ailtonmartins.accountservice.infrastructure.operations.KafkaDlqMonitor;
import com.ailtonmartins.accountservice.infrastructure.outbox.JpaOutboxEventRepository;
import com.ailtonmartins.accountservice.infrastructure.persistence.repository.JpaProcessedTransferEventRepository;
import com.ailtonmartins.accountservice.presentation.dto.response.AccountOperationSummaryResponse;
import com.ailtonmartins.accountservice.presentation.dto.response.OutboxEventResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/operations/accounts")
public class AccountOperationsController {

    private final JpaProcessedTransferEventRepository processedTransferEventRepository;
    private final JpaOutboxEventRepository outboxEventRepository;
    private final KafkaDlqMonitor kafkaDlqMonitor;
    private final String transferRequestedDlqTopic;

    public AccountOperationsController(
            JpaProcessedTransferEventRepository processedTransferEventRepository,
            JpaOutboxEventRepository outboxEventRepository,
            KafkaDlqMonitor kafkaDlqMonitor,
            @Value("${app.kafka.topics.transfer-requested}.dlq") String transferRequestedDlqTopic
    ) {
        this.processedTransferEventRepository = processedTransferEventRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaDlqMonitor = kafkaDlqMonitor;
        this.transferRequestedDlqTopic = transferRequestedDlqTopic;
    }

    @GetMapping("/summary")
    public AccountOperationSummaryResponse summary() {
        return new AccountOperationSummaryResponse(
                processedTransferEventRepository.countByStatus("COMPLETED"),
                processedTransferEventRepository.countByStatus("FAILED"),
                outboxEventRepository.countByStatus("PENDING"),
                outboxEventRepository.countByStatus("FAILED"),
                Map.of(transferRequestedDlqTopic, kafkaDlqMonitor.countMessages(transferRequestedDlqTopic))
        );
    }

    @GetMapping("/outbox/failed")
    public List<OutboxEventResponse> failedOutboxEvents() {
        return outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc("FAILED")
                .stream()
                .map(OutboxEventResponse::from)
                .toList();
    }
}
