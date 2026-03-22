package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.CartEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity,Integer> {
    Optional<CartEntity> findByUsername(String username);

    Optional<CartEntity> findByUsernameAndIsActiveTrue(String username);

    @Modifying
    @Query("UPDATE CartEntity c SET c.isActive = false WHERE c.username = :username")
    int deactivateCart(String username);

    Optional<CartEntity> findByCartId(UUID cartId);
}
