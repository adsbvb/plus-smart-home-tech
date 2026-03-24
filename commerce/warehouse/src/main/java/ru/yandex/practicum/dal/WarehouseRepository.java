package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.WarehouseProductEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProductEntity,Integer> {
    boolean existsByProductId(UUID productId);

    List<WarehouseProductEntity> findByProductIdIn(List<UUID> productIds);

    Optional<WarehouseProductEntity> findByProductId(UUID productId);
}
