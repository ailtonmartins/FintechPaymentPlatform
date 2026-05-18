package com.ailtonmartins.accountservice.application.port;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;

public interface TransferResultPublisher {

    void publishCompleted(ProcessTransferCommand command);

    void publishFailed(ProcessTransferCommand command, String failureReason);
}
