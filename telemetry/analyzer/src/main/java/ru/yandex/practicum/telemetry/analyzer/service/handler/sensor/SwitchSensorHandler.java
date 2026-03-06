package ru.yandex.practicum.telemetry.analyzer.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

@Component
public class SwitchSensorHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return SwitchSensorAvro.class.getName();
    }

    @Override
    public Integer getValue(ConditionType type, SensorStateAvro state) {
        SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) state.getData();
        return switch (type) {
            case SWITCH -> switchSensorAvro.getState() ? 1 : 0;
            default -> 0;
        };
    }
}
