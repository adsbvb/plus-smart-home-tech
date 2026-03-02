package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SnapshotAggregatorServiceImpl implements SnapshotAggregatorService {

    private final Map<String, SensorsSnapshotAvro> snapshotsMap = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        log.debug("Обработка события датчика: hubId={}, sensorId={}, timestamp={}, payload={}",
                hubId, sensorId, event.getTimestamp(), event.getPayload());

        SensorsSnapshotAvro snapshotAvro = snapshotsMap.getOrDefault(
                hubId,
                createNewSnapshot(hubId, event)
        );

        SensorStateAvro oldState = snapshotAvro.getSensorsState().get(sensorId);

        if (oldState != null) {
            if (oldState.getTimestamp().isAfter(event.getTimestamp())) {
                log.debug("Игнорирование устаревшего события для датчика {}: " +
                                "timestamp события={} старше существующего timestamp={}",
                        sensorId, event.getTimestamp(), oldState.getTimestamp());
                return Optional.empty();
            }
            if (oldState.getData().equals(event.getPayload())) {
                log.debug("Игнорирование дублирующихся данных для датчика {}: данные={} не изменились",
                        sensorId, event.getPayload());
                return Optional.empty();
            }
        }

        SensorStateAvro newState = createNewSensorState(event);

        snapshotAvro.getSensorsState().put(sensorId, newState);
        snapshotAvro.setTimestamp(event.getTimestamp());

        snapshotsMap.put(hubId, snapshotAvro);

        log.info("Обновлен снепшот для хаба {}: Новые данные для датчика {}",
                hubId, sensorId);
        log.debug("Текущее состояние снепшота для хаба {}: {}", hubId, snapshotAvro);

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
