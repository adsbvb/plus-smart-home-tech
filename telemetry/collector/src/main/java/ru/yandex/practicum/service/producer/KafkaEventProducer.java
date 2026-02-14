package ru.yandex.practicum.service.producer;

import org.apache.avro.specific.SpecificRecordBase;

import java.time.Instant;

public interface KafkaEventProducer {
    void send(SpecificRecordBase message, String hubId, Instant timestamp, String topic);
}
