package com.ailtonmartins.accountservice.application.result;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;

public record ProcessTransferResult(
        ProcessTransferCommand command,
        String status,
        String failureReason,
        boolean alreadyProcessed
) {

    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";

    public static ProcessTransferResult completed(ProcessTransferCommand command, boolean alreadyProcessed) {
        return new ProcessTransferResult(command, COMPLETED, null, alreadyProcessed);
    }

    public static ProcessTransferResult failed(
            ProcessTransferCommand command,
            String failureReason,
            boolean alreadyProcessed
    ) {
        return new ProcessTransferResult(command, FAILED, failureReason, alreadyProcessed);
    }

    public boolean completed() {
        return COMPLETED.equals(status);
    }
}
