package ru.yandex.practicum.telemetry.analyzer.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

public class LightSensorHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return LightSensorAvro.class.getName();
    }

    @Override
    public Integer getValue(ConditionType type, SensorStateAvro state) {
        LightSensorAvro lightSensorAvro = (LightSensorAvro) state.getData();
        return switch (type) {
            case LUMINOSITY -> lightSensorAvro.getLuminosity();
            default -> null;
        };
    }
}