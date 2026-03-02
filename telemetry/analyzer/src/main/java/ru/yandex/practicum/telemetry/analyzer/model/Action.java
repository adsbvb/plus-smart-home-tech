package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "actions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActionType type;

    @Column(name = "value")
    private Integer value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        return id != null && id.equals(((Action) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}