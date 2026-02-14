package ru.yandex.practicum.service.producer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Properties;

import static ru.yandex.practicum.configuration.KafkaConfig.*;

@Slf4j
@Component
public class KafkaEventProducerImpl implements KafkaEventProducer {

    private Producer<String, SpecificRecordBase> producer;

    @PostConstruct
    public void init() {
            initProducer();
    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER_CLASS);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS);

        producer = new KafkaProducer<>(config);
        log.info("Kafka producer инициализирован с bootstrap.servers = {}", config.get("bootstrap.servers"));
    }

    @Override
    public void send(SpecificRecordBase message, String hubId, Instant timestamp, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic, null, timestamp.toEpochMilli(), hubId, message);
        log.info("Отправка сообщения в Kafka: topic={}, hubId={}", topic, hubId);
        log.debug("Содержимое: {}", message);
        producer.send(record);
        producer.flush();
    }

    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
