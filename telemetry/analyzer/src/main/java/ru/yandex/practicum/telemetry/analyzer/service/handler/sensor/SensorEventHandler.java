package ru.yandex.practicum.telemetry.analyzer.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

public interface SensorEventHandler {
    String getType();

    Integer getValue(ConditionType condition,  SensorStateAvro snapshot);
}
