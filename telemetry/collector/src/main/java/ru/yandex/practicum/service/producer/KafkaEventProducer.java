package ru.yandex.practicum.service.producer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

import java.time.Instant;

public interface KafkaEventProducer {
    Producer<String, SpecificRecordBase> getProducer();

    void send(SpecificRecordBase message, String hubId, Instant timestamp, String topic);

    void stop();
}
