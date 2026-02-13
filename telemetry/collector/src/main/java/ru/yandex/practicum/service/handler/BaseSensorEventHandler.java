package ru.yandex.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

import static ru.yandex.practicum.configuration.KafkaConfig.SENSOR_EVENTS;

@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private final KafkaEventProducer producer;

    @Override
    public void handle(SensorEvent event) {
        // Проверка соответсвия типа события ожидаемому типу обрботчика
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизветсный тип события: " + event.getType());
        }

        // Преобразование событие в Avro-запись
        T payload = mapToAvro(event);

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), SENSOR_EVENTS);
    }

    public abstract T mapToAvro(SensorEvent event);
}
