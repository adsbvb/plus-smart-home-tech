package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioCondition {

    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id")
    private Condition condition;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioConditionId implements Serializable {

        @Column(name = "scenario_id")
        private Long scenarioId;
        @Column(name = "sensor_id")
        private String sensorId;
        @Column(name = "condition_id")
        private Long conditionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScenarioConditionId that = (ScenarioConditionId) o;
            return Objects.equals(scenarioId, that.scenarioId)
                    && Objects.equals(sensorId, that.sensorId)
                    && Objects.equals(conditionId, that.conditionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scenarioId, sensorId, conditionId);
        }
    }
}