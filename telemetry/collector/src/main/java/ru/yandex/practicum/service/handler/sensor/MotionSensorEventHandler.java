package ru.yandex.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.MotionSensorEvent;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.model.SensorEventType;
import ru.yandex.practicum.service.handler.BaseSensorEventHandler;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {

    protected MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEvent event) {
        MotionSensorEvent motionSensorEvent = (MotionSensorEvent) event;
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
