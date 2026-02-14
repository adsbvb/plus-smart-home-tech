package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NotNull
public class ScenarioCondition {

    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Integer value;
}
