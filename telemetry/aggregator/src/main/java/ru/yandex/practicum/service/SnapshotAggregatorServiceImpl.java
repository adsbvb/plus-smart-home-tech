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
        SensorsSnapshotAvro sensorsSnapshotAvro = snapshotsMap.getOrDefault(
                event.getId(),
                createNewSnapshot(event.getHubId(), event)
        );
        /*Проверяем, есть ли снапшот для event.getHubId()
        Если снапшот есть, то достаём его
        Если нет, то создаём новый

        Проверяем, есть ли в снапшоте данные для event.getId()
        Если данные есть, то достаём их в переменную oldState
        Проверка, если oldState.getTimestamp() произошёл позже, чем
        event.getTimestamp() или oldState.getData() равен
        event.getPayload(), то ничего обнавлять не нужно, выходим из метода
        вернув Optional.empty()

        // если дошли до сюда, значит, пришли новые данные и
        // снапшот нужно обновить
        Создаём экземпляр SensorStateAvro на основе данных события
        Добавляем полученный экземпляр в снапшот
        Обновляем таймстемп снапшота таймстемпом из события
        Возвращаем снапшот - Optional.of(snapshot)*/
    }

    private SensorsSnapshotAvro createNewSnapshot(String hubId, SensorEventAvro event) {
        Map<String, SensorStateAvro> stateAvroMap = new HashMap<>();
        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
