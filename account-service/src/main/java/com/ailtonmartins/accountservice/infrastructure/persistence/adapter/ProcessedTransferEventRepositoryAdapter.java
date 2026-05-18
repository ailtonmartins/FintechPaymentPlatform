package com.ailtonmartins.accountservice.infrastructure.persistence.adapter;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.port.ProcessedTransferEventRepository;
import com.ailtonmartins.accountservice.application.result.ProcessTransferResult;
import com.ailtonmartins.accountservice.infrastructure.persistence.entity.ProcessedTransferEventEntity;
import com.ailtonmartins.accountservice.infrastructure.persistence.repository.JpaProcessedTransferEventRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProcessedTransferEventRepositoryAdapter implements ProcessedTransferEventRepository {

    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";

    private final JpaProcessedTransferEventRepository repository;

    public ProcessedTransferEventRepositoryAdapter(JpaProcessedTransferEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ProcessTransferResult> findByTransactionId(UUID transactionId) {
        return repository.findById(transactionId).map(this::toResult);
    }

    @Override
    public ProcessTransferResult saveCompleted(ProcessTransferCommand command) {
        ProcessedTransferEventEntity entity = new ProcessedTransferEventEntity(
                command.transactionId(),
                command.sourceAccountId(),
                command.destinationAccountId(),
                command.amount(),
                COMPLETED,
                null,
                LocalDateTime.now()
        );

        return toResult(repository.save(entity));
    }

    @Override
    public ProcessTransferResult saveFailed(ProcessTransferCommand command, String failureReason) {
        ProcessedTransferEventEntity entity = new ProcessedTransferEventEntity(
                command.transactionId(),
                command.sourceAccountId(),
                command.destinationAccountId(),
                command.amount(),
                FAILED,
                normalizeFailureReason(failureReason),
                LocalDateTime.now()
        );

        return toResult(repository.save(entity));
    }

    private ProcessTransferResult toResult(ProcessedTransferEventEntity entity) {
        ProcessTransferCommand command = new ProcessTransferCommand(
                entity.getTransactionId(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                entity.getAmount()
        );

        return COMPLETED.equals(entity.getStatus())
                ? ProcessTransferResult.completed(command, false)
                : ProcessTransferResult.failed(command, entity.getFailureReason(), false);
    }

    private static String normalizeFailureReason(String failureReason) {
        if (failureReason == null || failureReason.isBlank()) {
            return "Falha ao processar transferencia";
        }
        return failureReason.trim();
    }
}
