package ru.yandex.practicum.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

import java.time.Instant;

import static ru.yandex.practicum.configuration.KafkaConfig.HUBS_EVENTS;

@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    private final KafkaEventProducer producer;

    protected BaseHubEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("Тип события: {}, HubId: {}, Timestamp: {}",
                event.getPayloadCase(), event.getHubId(), event.getTimestamp());

        // Проверка соответсвия типа события ожидаемому типу обрботчика
        if (!event.getPayloadCase().equals(getMessageType())) {
            log.error("Несоответствие типа события. Ожидался: {}, получен: {}",
                    getMessageType(), event.getPayloadCase());
            throw new IllegalArgumentException("Неизветсный тип события: " + event.getPayloadCase());
        }

        // Преобразование событие в Avro-запись
        T payload = mapToAvro(event);
        log.debug("Тип payload: {}", payload != null ? payload.getSchema().getName() : "null");

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
        log.debug("HubEventAvro создан, размер payload: {} байт",
                payload != null ? payload.toString().length() : 0);

        producer.send(eventAvro, event.getHubId(), eventAvro.getTimestamp(), HUBS_EVENTS);
        log.info("Отправлено в Kafka: topic={}, hubId={}", HUBS_EVENTS, event.getHubId());
    }

    public abstract T mapToAvro(HubEventProto event);
}
