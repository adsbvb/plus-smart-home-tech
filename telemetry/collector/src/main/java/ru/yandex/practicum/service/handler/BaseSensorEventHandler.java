package ru.yandex.practicum.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

import java.time.Instant;

import static ru.yandex.practicum.configuration.KafkaConfig.SENSOR_EVENTS_TOPIC;

@Slf4j
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private final KafkaEventProducer producer;

    protected BaseSensorEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Тип: {}, sensorId: {}, hubId: {}, timestamp: {}",
                event.getPayloadCase(), event.getId(), event.getHubId(), event.getTimestamp());

        // Проверка соответсвия типа события ожидаемому типу обрботчика
        if (!event.getPayloadCase().equals(getMessageType())) {
            log.error("Несоответствие типа события. Ожидался: {}, получен: {}",
                    getMessageType(), event.getPayloadCase());
            throw new IllegalArgumentException("Неизветсный тип события: " + event.getPayloadCase());
        }

        // Преобразование событие в Avro-запись
        T payload = mapToAvro(event);
        log.debug("Тип payload: {}", payload != null ? payload.getSchema().getName() : "null");

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
        log.debug("SensorEventAvro создан, размер payload: {} байт",
                payload != null ? payload.toString().length() : 0);

        producer.send(eventAvro, event.getHubId(), eventAvro.getTimestamp(), SENSOR_EVENTS_TOPIC);
        log.info("Отправлено в Kafka: topic={}, hubId={}", SENSOR_EVENTS_TOPIC, event.getHubId());
    }

    public abstract T mapToAvro(SensorEventProto event);
}
