package ru.yandex.practicum.telemetry.analyzer.service.handler;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

public class ScenarioRemovedHandler implements HubEventHandler {
    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {

    }
}