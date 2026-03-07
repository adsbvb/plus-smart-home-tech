package ru.yandex.practicum.telemetry.analyzer.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.*;
import ru.yandex.practicum.telemetry.analyzer.mapper.EnumMapper;
import ru.yandex.practicum.telemetry.analyzer.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro  event) {
        String hubId = event.getHubId();
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();
        String scenarioName = payload.getName();

        log.info("Добавление сценария: hubId={}, scenarioName={}, условий={}, действий={}",
                hubId, scenarioName,
                payload.getConditions().size(),
                payload.getActions().size());

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .map(existing -> {
                    conditionRepository.deleteById(existing.getId());
                    actionRepository.deleteById(existing.getId());
                    return existing;
                })
                .orElseGet(() -> {
                    Scenario newScenario = Scenario.builder()
                            .hubId(hubId)
                            .name(scenarioName)
                            .build();
                    return scenarioRepository.save(newScenario);
                });

        saveConditions(scenario, payload.getConditions(), hubId);
        saveActions(scenario, payload.getActions(), hubId);

        log.info("Сценарий {} успешно сохранен для хаба {}", scenarioName, hubId);
    }

    private void saveConditions(Scenario scenario, List<ScenarioConditionAvro> conditionAvros, String hubId) {

        if (conditionAvros.isEmpty()) {
            return;
        }

        Set<String> sensorIds = conditionAvros.stream()
                .map(ScenarioConditionAvro::getSensorId)
                .collect(Collectors.toSet());

        Map<String, Sensor> sensorMap = sensorRepository.findAllByIdInAndHubId(sensorIds, hubId).stream()
                .collect(Collectors.toMap(Sensor::getId, Function.identity()));

        for (String sensorId : sensorIds) {
            if (!sensorMap.containsKey(sensorId)) {
                throw new RuntimeException("Датчик не найден: " + sensorId);
            }
        }

        List<Condition> conditionList = new ArrayList<>();

        for (ScenarioConditionAvro conditionAvro : conditionAvros) {

            Condition condition = Condition.builder()
                    .type(EnumMapper.toConditionType(conditionAvro.getType()))
                    .operation(EnumMapper.toConditionOperation(conditionAvro.getOperation()))
                    .value(convertValue(conditionAvro.getValue()))
                    .build();

            conditionList.add(condition);
        }

        List<Condition> savedConditions = conditionRepository.saveAll(conditionList);
        List<ScenarioCondition> scenarioConditionList = new ArrayList<>();

        for (int i = 0; i < savedConditions.size(); i++) {
            ScenarioConditionAvro conditionAvro = conditionAvros.get(i);
            Sensor sensor = sensorMap.get(conditionAvro.getSensorId());
            Condition condition = savedConditions.get(i);

            ScenarioCondition.ScenarioConditionId id = new ScenarioCondition.ScenarioConditionId(
                    scenario.getId(),
                    sensor.getId(),
                    condition.getId()
            );

            ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                    .id(id)
                    .scenario(scenario)
                    .sensor(sensor)
                    .condition(condition)
                    .build();

            scenarioConditionList.add(scenarioCondition);
        }

        scenarioConditionRepository.saveAll(scenarioConditionList);
    }

    private Integer convertValue(Object value) {
        return switch (value) {
            case null -> 0;
            case Integer i -> i;
            case Boolean b -> b ? 1 : 0;
            default -> Integer.parseInt(value.toString());
        };
    }

    private void saveActions(Scenario scenario, List<DeviceActionAvro> actionAvros, String hubId) {

        if (actionAvros.isEmpty()) {
            return;
        }

        Set<String> sensorIds = actionAvros.stream()
                .map(DeviceActionAvro::getSensorId)
                .collect(Collectors.toSet());

        Map<String, Sensor> sensorMap = sensorRepository.findAllByIdInAndHubId(sensorIds, hubId).stream()
                .collect(Collectors.toMap(Sensor::getId, Function.identity()));

        for (String sensorId : sensorIds) {
            if (!sensorMap.containsKey(sensorId)) {
                throw new RuntimeException("Датчик не найден: " + sensorId);
            }
        }

        List<Action> actionList = new ArrayList<>();

        for (DeviceActionAvro actionAvro : actionAvros) {

            Action action = Action.builder()
                    .type(EnumMapper.toActionType(actionAvro.getType()))
                    .value(actionAvro.getValue() != null ? actionAvro.getValue() : 0)
                    .build();

            actionList.add(action);
        }

        List<Action> savedActions = actionRepository.saveAll(actionList);
        List<ScenarioAction> scenarioActionList = new ArrayList<>();

        for (int i = 0; i < savedActions.size(); i++) {
            DeviceActionAvro deviceActionAvro = actionAvros.get(i);
            Sensor sensor = sensorMap.get(deviceActionAvro.getSensorId());
            Action action = savedActions.get(i);

            ScenarioAction.ScenarioActionId id = new ScenarioAction.ScenarioActionId(
                    scenario.getId(),
                    sensor.getId(),
                    action.getId()
            );

            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(id)
                    .scenario(scenario)
                    .sensor(sensor)
                    .action(action)
                    .build();

            scenarioActionList.add(scenarioAction);
        }
        scenarioActionRepository.saveAll(scenarioActionList);
    }
}