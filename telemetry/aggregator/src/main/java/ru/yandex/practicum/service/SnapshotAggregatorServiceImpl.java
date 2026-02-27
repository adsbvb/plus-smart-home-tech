package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SnapshotAggregatorServiceImpl implements SnapshotAggregatorService {

    private final Map<String, SensorsSnapshotAvro> snapshotsMap = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        SensorsSnapshotAvro snapshotAvro = snapshotsMap.getOrDefault(
                hubId,
                createNewSnapshot(hubId, event)
        );

        SensorStateAvro oldState = snapshotAvro.getSensorsState().get(sensorId);

        if (oldState != null) {
            if (oldState.getTimestamp().isAfter(event.getTimestamp())) {
                return Optional.empty();
            }
            if (oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = createNewSensorState(event);

        snapshotAvro.getSensorsState().put(sensorId, newState);
        snapshotAvro.setTimestamp(event.getTimestamp());

        snapshotsMap.put(hubId, snapshotAvro);

        return Optional.of(snapshotAvro);
    }

    private SensorsSnapshotAvro createNewSnapshot(String hubId, SensorEventAvro event) {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(event.getTimestamp())
                .setSensorsState(new HashMap<>())
                .build();
    }

    private SensorStateAvro createNewSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
