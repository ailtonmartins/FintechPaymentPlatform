CREATE TABLE processed_transfer_events (
    transaction_id UUID PRIMARY KEY,
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    failure_reason VARCHAR(500),
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_processed_transfer_events_amount_positive
        CHECK (amount > 0),
    CONSTRAINT chk_processed_transfer_events_status
        CHECK (status IN ('COMPLETED', 'FAILED')),
    CONSTRAINT chk_processed_transfer_events_failure_reason
        CHECK (
            (status = 'FAILED' AND failure_reason IS NOT NULL)
            OR (status = 'COMPLETED' AND failure_reason IS NULL)
        )
);

CREATE INDEX idx_processed_transfer_events_status ON processed_transfer_events (status);
CREATE INDEX idx_processed_transfer_events_processed_at ON processed_transfer_events (processed_at);
