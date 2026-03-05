package ru.yandex.practicum.telemetry.analyzer.service.handler.sensor;

public interface SensorEventHandler {
    String getType();

    Integer getValue();
}
