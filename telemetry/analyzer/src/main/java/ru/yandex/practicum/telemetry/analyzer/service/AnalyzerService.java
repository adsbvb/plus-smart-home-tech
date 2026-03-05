package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.service.handler.sensor.SensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyzerService {

    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorEventHandler> sensorEventHandlers;

    public AnalyzerService(ScenarioRepository scenarioRepository, Set<SensorEventHandler> handlers) {
        this.scenarioRepository = scenarioRepository;
        this.sensorEventHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getType,
                        Function.identity()
                ));
    }

    public void analyze(SensorsSnapshotAvro snapshot) {

        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        if (scenarios.isEmpty()) {
            log.debug("Для хаба {} сценарии не найдены", hubId);
            return;
        }

        log.info("Найдено {} сценариев для хаба {}", scenarios.size(), hubId);

        for (Scenario scenario : scenarios) {
            checkScenario(scenario, snapshot);
        }
    }

    private void checkScenario(Scenario scenario, SensorsSnapshotAvro snapshot) {
        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();
        for (ScenarioCondition condition : scenario.getConditions()) {
            checkCondition(condition, sensorStates);
        }
    }

    private boolean checkCondition(ScenarioCondition condition, Map<String, SensorStateAvro> sensorStates) {
        SensorStateAvro state = sensorStates.get(condition.getSensor().getId());
        String type = state.getData().getClass().getName();

        if (!sensorEventHandlers.containsKey(type)) {
            throw new IllegalArgumentException("Не найден обработчик для сенсора " + type);
        }

        SensorEventHandler sensorEventHandler = sensorEventHandlers.get(type);
        Integer value = sensorEventHandler.getValue(condition.getCondition().getType(), state);
    }
}
