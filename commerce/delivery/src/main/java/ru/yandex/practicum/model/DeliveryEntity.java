package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "delivery")
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_address_id")
    private AddressEntity fromAddress;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_address_id")
    private AddressEntity toAddress;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false, length = 20)
    private DeliveryState deliveryState;
}
