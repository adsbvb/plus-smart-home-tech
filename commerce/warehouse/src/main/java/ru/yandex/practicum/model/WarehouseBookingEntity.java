package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "warehouse_booking")
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseBookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "product_id")
    private UUID productId;

    private Long quantity;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
}

