package ru.yandex.practicum.telemetry.analyzer.service.handler;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

public class ScenarioAddedHandler implements HubEventHandler {
    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {

    }
}