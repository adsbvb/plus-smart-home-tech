package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

@Component
public class SnapshotAggregatorServiceImpl implements SnapshotAggregatorService {

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        return Optional.empty();
    }
}
