package ru.yandex.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.configuration.KafkaProperties;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.service.handler.BaseSensorEventHandler;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {

    protected SwitchSensorEventHandler(KafkaEventProducer producer, KafkaProperties kafkaProperties) {
        super(producer, kafkaProperties);
    }

    @Override
    public SwitchSensorAvro mapToAvro(SensorEventProto event) {
        SwitchSensorProto switchSensorEvent = event.getSwitchSensor();
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.getState())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}
