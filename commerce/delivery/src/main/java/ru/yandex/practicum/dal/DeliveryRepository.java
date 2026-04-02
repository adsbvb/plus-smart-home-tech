package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.DeliveryEntity;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Integer> {
    Optional<DeliveryEntity> findDeliveryByOrderId(UUID orderId);
}
