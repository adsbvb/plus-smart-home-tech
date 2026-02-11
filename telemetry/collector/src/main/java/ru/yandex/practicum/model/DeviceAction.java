package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NotNull
public class DeviceAction {

    String sensorId;

    ActionType type;

    int value;
}
