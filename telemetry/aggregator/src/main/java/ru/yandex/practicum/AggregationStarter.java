package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotAggregatorService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.yandex.practicum.configuration.KafkaSnapshotConfiguration.SENSOR_EVENTS_TOPIC;
import static ru.yandex.practicum.configuration.KafkaSnapshotConfiguration.SNAPSHOTS_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final Consumer<String, SensorEventAvro> consumer;
    private final Producer<String, SensorsSnapshotAvro> producer;

    private final SnapshotAggregatorService snapshotAggregatorService;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private static final List<String> TOPICS = List.of(SENSOR_EVENTS_TOPIC);

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Получен сигнал завершения, инициируем остановку...");
            consumer.wakeup();
        }));
        try {
            consumer.subscribe(TOPICS);
            while (true) {
                log.debug("Ожидание новых сообщений...");
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                int count = 0;
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    log.debug("Обработка записи: topic={}, partition={}, offset={}, key={}",
                            record.topic(), record.partition(), record.offset(), record.key());
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
                producer.flush();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
                log.info("AggregationStarter завершил работу");
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorEventAvro> record) throws InterruptedException {
        try {
            Optional<SensorsSnapshotAvro> snapshotOpt = snapshotAggregatorService.updateState(record.value());
            snapshotOpt.ifPresent(this::sendSnapshot);
        } catch (Exception e) {
            log.error("Ошибка обработки записи {}: {} ",record.offset(), e.getMessage(), e);
        }
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Отправка снепшота для хаба {} в топик {}", snapshot.getHubId(), SNAPSHOTS_TOPIC);
        log.debug("Детали снепшота: timestamp={}, количество датчиков={}",
                snapshot.getTimestamp(), snapshot.getSensorsState().size());

        try {
            ProducerRecord<String, SensorsSnapshotAvro> record = new ProducerRecord<>(
                    SNAPSHOTS_TOPIC,
                    null,
                    snapshot.getTimestamp().toEpochMilli(),
                    snapshot.getHubId(),
                    snapshot);
            producer.send(record);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения в топик: {}", e.getMessage(), e);
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorEventAvro> record, int count, Consumer<String, SensorEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if(count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}
