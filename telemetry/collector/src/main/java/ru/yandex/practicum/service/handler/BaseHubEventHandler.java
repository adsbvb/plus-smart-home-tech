package ru.yandex.practicum.service.handler;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.DeviceAddedEvent;
import ru.yandex.practicum.model.HubEvent;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    @Override
    public void handle(HubEvent event) {
        // Проверка соответсвия типа события ожидаемому типу обрботчика
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизветсный тип события: " + event.getType());
        }

        // Преобразование событие в Avro-запись
        T payload = mapToAvro(event);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), HUBS_EVENTS);
    }

    public abstract T mapToAvro(HubEvent event);
}
