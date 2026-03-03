package ru.yandex.practicum.telemetry.analyzer.service.handler;

import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class DeviceRemovedHandler implements HubEventHandler {
    @Override
    public String getType() {
        return DeviceRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {

    }
}