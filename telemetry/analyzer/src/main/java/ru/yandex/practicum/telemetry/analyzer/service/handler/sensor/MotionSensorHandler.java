package ru.yandex.practicum.telemetry.analyzer.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

public class MotionSensorHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return MotionSensorAvro.class.getName();
    }

    @Override
    public Integer getValue(ConditionType type, SensorStateAvro state) {
        MotionSensorAvro motionSensorAvro = (MotionSensorAvro) state.getData();
        return switch (type) {
            case MOTION -> motionSensorAvro.getMotion() ? 1 : 0;
            default -> null;
        };
    }
}
