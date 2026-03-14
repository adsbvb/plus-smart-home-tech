package ru.yandex.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.configuration.KafkaProperties;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.service.handler.BaseSensorEventHandler;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {

    protected MotionSensorEventHandler(KafkaEventProducer producer, KafkaProperties kafkaProperties) {
        super(producer, kafkaProperties);
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto motionSensorEvent = event.getMotionSensor();
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }
}
