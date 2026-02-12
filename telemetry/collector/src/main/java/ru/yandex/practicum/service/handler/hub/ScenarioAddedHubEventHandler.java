package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.EnumMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.handler.BaseHubEventHandler;
import ru.yandex.practicum.service.producer.KafkaEventProducer;

@Component(value = "SCENARIO_ADDED")
public class ScenarioAddedHubEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(
                        scenarioAddedEvent.getConditions().stream().map(this::mapToCondition).toList())  // array<ScenarioConditionAvro>
                .setActions(
                        scenarioAddedEvent.getActions().stream().map(this::mapToDeviceAction).toList())    // array<DeviceActionAvro>
                .build();
    }

    private ScenarioConditionAvro mapToCondition(ScenarioCondition scenarioCondition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(EnumMapper.toConditionTypeAvro(scenarioCondition.getType()))
                .setOperation(EnumMapper.toConditionOperationTypeAvro(scenarioCondition.getOperation()))
                .setValue(scenarioCondition.getValue())
                .build();
    }

    private DeviceActionAvro mapToDeviceAction(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(EnumMapper.toActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }
}
