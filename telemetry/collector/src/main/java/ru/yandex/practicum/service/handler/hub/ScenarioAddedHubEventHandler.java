package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
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
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(
                        scenarioAddedEvent.getConditionsList().stream().map(this::mapToCondition).toList())  // array<ScenarioConditionAvro>
                .setActions(
                        scenarioAddedEvent.getActionsList().stream().map(this::mapToDeviceAction).toList())    // array<DeviceActionAvro>
                .build();
    }

    private ScenarioConditionAvro mapToCondition(ScenarioConditionProto scenarioCondition) {
        Object value = null;

        if (scenarioCondition.hasBoolValue()) {
            value = scenarioCondition.getBoolValue();
        } else if (scenarioCondition.hasIntValue()) {
            value = scenarioCondition.getIntValue();
        }

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(EnumMapper.toConditionTypeAvro(
                        ConditionType.valueOf(scenarioCondition.getType().name())))
                .setOperation(EnumMapper.toConditionOperationTypeAvro(
                        ConditionOperation.valueOf(scenarioCondition.getOperation().name())))
                .setValue(value)
                .build();
    }

    private DeviceActionAvro mapToDeviceAction(DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(EnumMapper.toActionTypeAvro(
                        ActionType.valueOf(deviceAction.getType().name())))
                .setValue(deviceAction.getValue())
                .build();
    }
}
