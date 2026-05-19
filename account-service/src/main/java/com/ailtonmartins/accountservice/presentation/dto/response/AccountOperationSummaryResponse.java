package com.ailtonmartins.accountservice.presentation.dto.response;

import java.util.Map;

public record AccountOperationSummaryResponse(
        long completedTransfers,
        long failedTransfers,
        long pendingOutboxEvents,
        long failedOutboxEvents,
        Map<String, Long> dlqMessages
) {
}
