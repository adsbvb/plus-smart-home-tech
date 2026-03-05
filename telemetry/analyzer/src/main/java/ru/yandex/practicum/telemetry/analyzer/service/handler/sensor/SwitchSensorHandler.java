package ru.yandex.practicum.telemetry.analyzer.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

public class SwitchSensorHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return SwitchSensorAvro.class.getName();
    }

    @Override
    public Integer getValue() {
        return 0;
    }
}
