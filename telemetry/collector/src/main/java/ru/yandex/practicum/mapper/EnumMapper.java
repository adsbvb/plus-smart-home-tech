package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.*;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class EnumMapper {

    private final Map<DeviceType, DeviceTypeAvro> DEVICE_TYPE_MAP = new EnumMap<>(DeviceType.class);
    private final Map<ActionType, ActionTypeAvro> ACTION_TYPE_MAP = new EnumMap<>(ActionType.class);
    private final Map<ConditionType, ConditionTypeAvro> CONDITION_TYPE_MAP = new EnumMap<>(ConditionType.class);
    private final Map<ConditionOperation, ConditionOperationAvro> CONDITION_OPERATION_MAP = new EnumMap<>(ConditionOperation.class);

    static {
        // DeviceType
        DEVICE_TYPE_MAP.put(DeviceType.LIGHT_SENSOR, DeviceTypeAvro.LIGHT_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceType.MOTION_SENSOR, DeviceTypeAvro.MOTION_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceType.SWITCH_SENSOR, DeviceTypeAvro.SWITCH_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceType.CLIMATE_SENSOR, DeviceTypeAvro.CLIMATE_SENSOR);
        DEVICE_TYPE_MAP.put(DeviceType.TEMPERATURE_SENSOR, DeviceTypeAvro.TEMPERATURE_SENSOR);

        // ActionType
        ACTION_TYPE_MAP.put(ActionType.ACTIVATE, ActionTypeAvro.ACTIVATE);
        ACTION_TYPE_MAP.put(ActionType.DEACTIVATE, ActionTypeAvro.DEACTIVATE);
        ACTION_TYPE_MAP.put(ActionType.INVERSE, ActionTypeAvro.INVERSE);
        ACTION_TYPE_MAP.put(ActionType.SET_VALUE, ActionTypeAvro.SET_VALUE);

        // ConditionType
        CONDITION_TYPE_MAP.put(ConditionType.MOTION, ConditionTypeAvro.MOTION);
        CONDITION_TYPE_MAP.put(ConditionType.LUMINOSITY, ConditionTypeAvro.LUMINOSITY);
        CONDITION_TYPE_MAP.put(ConditionType.SWITCH, ConditionTypeAvro.SWITCH);
        CONDITION_TYPE_MAP.put(ConditionType.TEMPERATURE, ConditionTypeAvro.TEMPERATURE);
        CONDITION_TYPE_MAP.put(ConditionType.CO2LEVEL, ConditionTypeAvro.CO2LEVEL);
        CONDITION_TYPE_MAP.put(ConditionType.HUMIDITY, ConditionTypeAvro.HUMIDITY);

        // ConditionOperation
        CONDITION_OPERATION_MAP.put(ConditionOperation.EQUALS, ConditionOperationAvro.EQUALS);
        CONDITION_OPERATION_MAP.put(ConditionOperation.GREATER_THAN, ConditionOperationAvro.GREATER_THAN);
        CONDITION_OPERATION_MAP.put(ConditionOperation.LOWER_THAN, ConditionOperationAvro.LOWER_THAN);
    }

    public DeviceTypeAvro toDeviceTypeAvro(DeviceType type) {
        DeviceTypeAvro result = DEVICE_TYPE_MAP.get(type);
        if (result == null) {
            throw new IllegalArgumentException("Неизвестно: " + type);
        }
        return result;
    }

    public static ActionTypeAvro toActionTypeAvro(ActionType type) {
        ActionTypeAvro result = ACTION_TYPE_MAP.get(type);
        if (result == null) {
            throw new IllegalArgumentException("Неизвестно: " + type);
        }
        return result;
    }

    public ConditionTypeAvro toConditionTypeAvro(ConditionType type) {
        ConditionTypeAvro result = CONDITION_TYPE_MAP.get(type);
        if (result == null) {
            throw new IllegalArgumentException("Неизвестно: " + type);
        }
        return result;
    }

    public ConditionOperationAvro toConditionOperationTypeAvro(ConditionOperation type) {
        ConditionOperationAvro result = CONDITION_OPERATION_MAP.get(type);
        if (result == null) {
            throw new IllegalArgumentException("Неизвестно: " + type);
        }
        return result;
    }
}
