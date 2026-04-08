package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.PaymentState;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "product_cost")
    private Double productCost;

    @Column(name = "delivery_cost")
    private Double deliveryCost;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "fee_total")
    private Double feeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state")
    @Builder.Default
    private PaymentState state = PaymentState.PENDING;
}
