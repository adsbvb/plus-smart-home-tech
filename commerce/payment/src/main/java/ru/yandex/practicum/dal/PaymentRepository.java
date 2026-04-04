package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.PaymentEntity;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    Optional<PaymentEntity> findByPaymentId(UUID paymentId);
}
