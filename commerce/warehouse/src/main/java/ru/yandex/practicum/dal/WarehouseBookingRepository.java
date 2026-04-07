package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.WarehouseBookingEntity;

import java.util.List;
import java.util.UUID;

public interface WarehouseBookingRepository extends JpaRepository<WarehouseBookingEntity,Integer> {
    List<WarehouseBookingEntity> findByOrderId(UUID orderId);
}
