package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "order_products")
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private OrderEntity order;

    @Column(name = "product_id")
    private UUID productId;

    private Long quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderProductEntity)) return false;
        return id != null && id.equals(((OrderProductEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}