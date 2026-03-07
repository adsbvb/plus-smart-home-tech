package ru.yandex.practicum.telemetry.analyzer.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.ScenarioActionRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.ScenarioConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.ScenarioRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro event) {
        String hubId = event.getHubId();
        ScenarioRemovedEventAvro payload = (ScenarioRemovedEventAvro) event.getPayload();
        String scenarioName = payload.getName();

        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresentOrElse(scenario -> {
                    scenarioConditionRepository.deleteByScenarioId(scenario.getId());
                    scenarioActionRepository.deleteByScenarioId(scenario.getId());

                    scenarioRepository.delete(scenario);

                    log.info("Сценарий {} удален из хаба {}", scenarioName, hubId);
                },
                        () -> log.warn("Сценарий {} не найден в хабе {}", scenarioName, hubId)
                );
    }
}