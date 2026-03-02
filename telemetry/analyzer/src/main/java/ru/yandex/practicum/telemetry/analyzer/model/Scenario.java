package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenarios")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scenario)) return false;
        return id != null && id.equals(((Scenario) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}