package ru.yandex.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.model.SensorEventType;
import ru.yandex.practicum.model.TemperatureSensorEvent;
import ru.yandex.practicum.service.handler.BaseSensorEventHandler;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {

    protected TemperatureSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public TemperatureSensorAvro mapToAvro(SensorEvent event) {
        TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) event;
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
