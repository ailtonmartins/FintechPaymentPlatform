package com.ailtonmartins.transactionservice.infrastructure.event;

import java.util.UUID;

public record TransferFailedEvent(UUID transactionId, String failureReason) {
}
