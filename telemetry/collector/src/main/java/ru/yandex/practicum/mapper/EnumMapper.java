package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class EnumMapper {

    private final Map<DeviceTypeProto, DeviceTypeAvro> DEVICE_TYPE_MAP = new EnumMap<>(DeviceTypeProto.class);
    private final Map<ActionTypeProto, ActionTypeAvro> ACTION_TYPE_MAP = new EnumMap<>(ActionTypeProto.class);
    private final Map<ConditionTypeProto, ConditionTypeAvro> CONDITION_TYPE_MAP = new EnumMap<>(ConditionTypeProto.class);
    private final Map<ConditionOperationProto, ConditionOperationAvro> CONDITION_OPERATION_MAP = new EnumMap<>(
            ConditionOperationProto.class);

    static {
        // DeviceType
        DEVICE_TYPE_MAP.put(DeviceTypeProto.LIGHT_SENSOR, DeviceTypeAvro.LIGHT_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceTypeProto.MOTION_SENSOR, DeviceTypeAvro.MOTION_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceTypeProto.SWITCH_SENSOR, DeviceTypeAvro.SWITCH_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceTypeProto.CLIMATE_SENSOR, DeviceTypeAvro.CLIMATE_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceTypeProto.TEMPERATURE_SENSOR, DeviceTypeAvro.TEMPERATURE_SENSOR);

        // ActionType
        ACTION_TYPE_MAP.put(ActionTypeProto.ACTIVATE, ActionTypeAvro.ACTIVATE);
        ACTION_TYPE_MAP.put(ActionTypeProto.DEACTIVATE, ActionTypeAvro.DEACTIVATE);
        ACTION_TYPE_MAP.put(ActionTypeProto.INVERSE, ActionTypeAvro.INVERSE);
        ACTION_TYPE_MAP.put(ActionTypeProto.SET_VALUE, ActionTypeAvro.SET_VALUE);

        // ConditionType
        CONDITION_TYPE_MAP.put(ConditionTypeProto.MOTION, ConditionTypeAvro.MOTION);
        CONDITION_TYPE_MAP.put(ConditionTypeProto.LUMINOSITY, ConditionTypeAvro.LUMINOSITY);
        CONDITION_TYPE_MAP.put(ConditionTypeProto.SWITCH, ConditionTypeAvro.SWITCH);
        CONDITION_TYPE_MAP.put(ConditionTypeProto.TEMPERATURE, ConditionTypeAvro.TEMPERATURE);
        CONDITION_TYPE_MAP.put(ConditionTypeProto.CO2LEVEL, ConditionTypeAvro.CO2LEVEL);
        CONDITION_TYPE_MAP.put(ConditionTypeProto.HUMIDITY, ConditionTypeAvro.HUMIDITY);

        // ConditionOperation
        CONDITION_OPERATION_MAP.put(ConditionOperationProto.EQUALS, ConditionOperationAvro.EQUALS);
        CONDITION_OPERATION_MAP.put(ConditionOperationProto.GREATER_THAN, ConditionOperationAvro.GREATER_THAN);
        CONDITION_OPERATION_MAP.put(ConditionOperationProto.LOWER_THAN, ConditionOperationAvro.LOWER_THAN);
    }

    public DeviceTypeAvro toDeviceTypeAvro(DeviceTypeProto type) {
        return DEVICE_TYPE_MAP.get(type);
    }

    public ActionTypeAvro toActionTypeAvro(ActionTypeProto type) {
        return ACTION_TYPE_MAP.get(type);
    }

    public ConditionTypeAvro toConditionTypeAvro(ConditionTypeProto type) {
        return CONDITION_TYPE_MAP.get(type);
    }

    public ConditionOperationAvro toConditionOperationTypeAvro(ConditionOperationProto type) {
        return CONDITION_OPERATION_MAP.get(type);
    }
}
