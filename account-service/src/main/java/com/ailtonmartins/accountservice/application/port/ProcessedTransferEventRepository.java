package com.ailtonmartins.accountservice.application.port;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.result.ProcessTransferResult;

import java.util.Optional;
import java.util.UUID;

public interface ProcessedTransferEventRepository {

    Optional<ProcessTransferResult> findByTransactionId(UUID transactionId);

    ProcessTransferResult saveCompleted(ProcessTransferCommand command);

    ProcessTransferResult saveFailed(ProcessTransferCommand command, String failureReason);
}
