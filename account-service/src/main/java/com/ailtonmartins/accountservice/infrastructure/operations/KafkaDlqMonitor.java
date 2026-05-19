package com.ailtonmartins.accountservice.infrastructure.operations;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class KafkaDlqMonitor {

    private final String bootstrapServers;

    public KafkaDlqMonitor(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public long countMessages(String topic) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        properties.put("group.id", "account-service-dlq-monitor");
        properties.put("enable.auto.commit", "false");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic, Duration.ofSeconds(2));
            if (partitionInfos == null || partitionInfos.isEmpty()) {
                return 0;
            }

            List<TopicPartition> partitions = partitionInfos.stream()
                    .map(partition -> new TopicPartition(topic, partition.partition()))
                    .toList();
            Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(partitions);
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);

            return partitions.stream()
                    .mapToLong(partition -> endOffsets.getOrDefault(partition, 0L)
                            - beginningOffsets.getOrDefault(partition, 0L))
                    .sum();
        }
    }
}
