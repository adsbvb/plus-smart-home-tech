package ru.yandex.practicum.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.model.LightSensorEvent;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.model.SensorEventType;
import ru.yandex.practicum.service.handler.BaseSensorEventHandler;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {

    protected LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public LightSensorAvro mapToAvro(SensorEvent event) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) event;
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
