package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioActions {

    @EmbeddedId
    private ScenarioActionId id;

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @MapsId("actionId")
    @JoinColumn(name = "action_id")
    private Action action;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioActionId implements Serializable {

        @Column(name = "scenario_id")
        private Long scenarioId;
        @Column(name = "sensor_id")
        private String sensorId;
        @Column(name = "action_id")
        private Long actionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScenarioActionId that = (ScenarioActionId) o;
            return Objects.equals(scenarioId, that.scenarioId)
                    && Objects.equals(sensorId, that.sensorId)
                    && Objects.equals(actionId, that.actionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scenarioId, sensorId, actionId);
        }

    }
}