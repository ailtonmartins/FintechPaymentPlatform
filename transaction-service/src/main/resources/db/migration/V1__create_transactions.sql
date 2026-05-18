CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    requester_user_id UUID NOT NULL,
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    failure_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_transactions_amount_positive
        CHECK (amount > 0),
    CONSTRAINT chk_transactions_type
        CHECK (type IN ('TRANSFER')),
    CONSTRAINT chk_transactions_status
        CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    CONSTRAINT chk_transactions_distinct_accounts
        CHECK (source_account_id <> destination_account_id),
    CONSTRAINT chk_transactions_failure_reason
        CHECK (
            (status = 'FAILED' AND failure_reason IS NOT NULL)
            OR (status <> 'FAILED' AND failure_reason IS NULL)
        )
);

CREATE INDEX idx_transactions_requester_user_id ON transactions (requester_user_id);
CREATE INDEX idx_transactions_source_account_id ON transactions (source_account_id);
CREATE INDEX idx_transactions_destination_account_id ON transactions (destination_account_id);
CREATE INDEX idx_transactions_status ON transactions (status);
CREATE INDEX idx_transactions_created_at ON transactions (created_at);
