package ru.yandex.practicum.telemetry.analyzer.service.handler;

import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class DeviceAddedHandler implements HubEventHandler {
    @Override
    public String getType() {
        return DeviceTypeAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {

    }
}