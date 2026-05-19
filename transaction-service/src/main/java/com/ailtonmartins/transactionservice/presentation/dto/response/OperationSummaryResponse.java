package com.ailtonmartins.transactionservice.presentation.dto.response;

import java.util.Map;

public record OperationSummaryResponse(
        long pendingTransactions,
        long failedTransactions,
        long pendingOutboxEvents,
        long failedOutboxEvents,
        Map<String, Long> dlqMessages
) {
}
