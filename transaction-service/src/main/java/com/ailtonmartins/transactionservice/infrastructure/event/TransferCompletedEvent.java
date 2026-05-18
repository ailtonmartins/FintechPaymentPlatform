package com.ailtonmartins.transactionservice.infrastructure.event;

import java.util.UUID;

public record TransferCompletedEvent(UUID transactionId) {
}
