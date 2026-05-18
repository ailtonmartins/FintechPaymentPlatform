package com.ailtonmartins.transactionservice.application.port;

import com.ailtonmartins.transactionservice.domain.model.Transaction;

public interface TransactionEventPublisher {

    void publishTransferRequested(Transaction transaction);
}
