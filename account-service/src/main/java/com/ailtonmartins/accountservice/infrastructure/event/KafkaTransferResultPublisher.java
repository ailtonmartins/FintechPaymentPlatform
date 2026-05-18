package com.ailtonmartins.accountservice.infrastructure.event;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.port.TransferResultPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransferResultPublisher implements TransferResultPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String transferCompletedTopic;
    private final String transferFailedTopic;

    public KafkaTransferResultPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.transfer-completed}") String transferCompletedTopic,
            @Value("${app.kafka.topics.transfer-failed}") String transferFailedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.transferCompletedTopic = transferCompletedTopic;
        this.transferFailedTopic = transferFailedTopic;
    }

    @Override
    public void publishCompleted(ProcessTransferCommand command) {
        kafkaTemplate.send(
                transferCompletedTopic,
                command.transactionId().toString(),
                TransferCompletedEvent.from(command)
        );
    }

    @Override
    public void publishFailed(ProcessTransferCommand command, String failureReason) {
        kafkaTemplate.send(
                transferFailedTopic,
                command.transactionId().toString(),
                TransferFailedEvent.from(command, failureReason)
        );
    }
}
