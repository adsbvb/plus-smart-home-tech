package ru.yandex.practicum.service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Properties;

import static ru.yandex.practicum.configuration.KafkaConfig.*;

@Component
@RequiredArgsConstructor
public class KafkaEventProducerImpl implements KafkaEventProducer {

    private Producer<String, SpecificRecordBase> producer;

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }
        return producer;
    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER_CLASS);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS);

        producer = new KafkaProducer<>(config);
    }

    @Override
    public void send(SpecificRecordBase message, String hubId, Instant timestamp, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic, null, timestamp.toEpochMilli(), hubId, message);
        producer.send(record);
        producer.flush();
    }

    @Override
    public void stop() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
