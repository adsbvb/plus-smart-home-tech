package ru.yandex.practicum.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.model.ProductEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity,Integer> {
    Optional<ProductEntity> findByProductId(UUID productId);

    boolean existsByProductIdAndProductState(UUID productId, ProductState productState);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.productState = 'DEACTIVATE', p.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE p.productId = :productId")
    int deactivateProduct(@Param("productId") UUID productId);

    @Modifying
    @Query("UPDATE ProductEntity  p SET p.quantityState = :quantityState, p.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE p.productId = :productId")
    int updateProductQuantity(@Param("productId") UUID productId,
            @Param("quantityState") QuantityState quantityState);

    Page<ProductEntity> findByProductCategory(ProductCategory category, Pageable pageable);
}
