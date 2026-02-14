package ru.yandex.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

import static ru.yandex.practicum.configuration.KafkaConfig.HUBS_EVENTS;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    private final KafkaEventProducer producer;

    @Override
    public void handle(HubEvent event) {
        log.info("Тип события: {}, HubId: {}, Timestamp: {}",
                event.getType(), event.getHubId(), event.getTimestamp());

        // Проверка соответсвия типа события ожидаемому типу обрботчика
        if (!event.getType().equals(getMessageType())) {
            log.error("Несоответствие типа события. Ожидался: {}, получен: {}",
                    getMessageType(), event.getType());
            throw new IllegalArgumentException("Неизветсный тип события: " + event.getType());
        }

        // Преобразование событие в Avro-запись
        T payload = mapToAvro(event);
        log.debug("Тип payload: {}", payload != null ? payload.getSchema().getName() : "null");

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
        log.debug("HubEventAvro создан, размер payload: {} байт",
                payload != null ? payload.toString().length() : 0);

        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), HUBS_EVENTS);
        log.info("Отправлено в Kafka: topic={}, hubId={}", HUBS_EVENTS, event.getHubId());
    }

    public abstract T mapToAvro(HubEvent event);
}
