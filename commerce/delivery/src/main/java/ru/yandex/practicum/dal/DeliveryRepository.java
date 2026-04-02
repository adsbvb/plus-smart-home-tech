package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.DeliveryEntity;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Integer> {
    DeliveryEntity findDeliveryByOrderId(UUID orderId);
}
