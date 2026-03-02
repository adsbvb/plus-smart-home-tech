package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConditionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation",  nullable = false)
    private ConditionOperation operation;

    @Column(name = "value")
    private Integer value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Condition)) return false;
        return id != null && id.equals(((Condition) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}