package ru.yandex.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

import static ru.yandex.practicum.configuration.KafkaConfig.SENSOR_EVENTS;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private final KafkaEventProducer producer;

    @Override
    public void handle(SensorEvent event) {
        log.info("Тип: {}, sensorId: {}, hubId: {}, timestamp: {}",
                event.getType(), event.getId(), event.getHubId(), event.getTimestamp());

        // Проверка соответсвия типа события ожидаемому типу обрботчика
        if (!event.getType().equals(getMessageType())) {
            log.error("Несоответствие типа события. Ожидался: {}, получен: {}",
                    getMessageType(), event.getType());
            throw new IllegalArgumentException("Неизветсный тип события: " + event.getType());
        }

        // Преобразование событие в Avro-запись
        T payload = mapToAvro(event);
        log.debug("Тип payload: {}", payload != null ? payload.getSchema().getName() : "null");

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
        log.debug("SensorEventAvro создан, размер payload: {} байт",
                payload != null ? payload.toString().length() : 0);

        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), SENSOR_EVENTS);
        log.info("Отправлено в Kafka: topic={}, hubId={}", SENSOR_EVENTS, event.getHubId());
    }

    public abstract T mapToAvro(SensorEvent event);
}
