package ru.yandex.practicum.telemetry.analyzer.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

public class TemperatureSensorHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return TemperatureSensorAvro.class.getName();
    }

    @Override
    public Integer getValue(ConditionType type, SensorStateAvro state) {
        TemperatureSensorAvro temperatureSensorAvro = (TemperatureSensorAvro) state.getData();
        return switch (type) {
            case TEMPERATURE -> temperatureSensorAvro.getTemperatureC();
            default -> null;
        };
    }
}
