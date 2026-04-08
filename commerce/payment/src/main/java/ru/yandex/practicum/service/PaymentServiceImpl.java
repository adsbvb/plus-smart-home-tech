package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dal.PaymentRepository;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.PaymentEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    private static final double TAX_RATE = 0.1;

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        log.info("Создание платежной системы для заказа: {}", order.getOrderId());

        if (order.getProductPrice() == null) {
            log.error("Цена товара для заказа не указана: {}", order.getOrderId());
            throw new IllegalArgumentException("Цена товара для заказа не указана");
        }

        if (order.getDeliveryPrice() == null) {
            log.error("Стоимость доставки для заказа не указана: {}", order.getOrderId());
            throw new IllegalArgumentException("Стоимость доставки для заказа не указана");
        }

        Double productCost = order.getProductPrice();
        Double deliveryCost = order.getDeliveryPrice();
        Double feeTotal = productCost * TAX_RATE;
        Double totalCost = productCost + feeTotal + deliveryCost;

        PaymentEntity payment = PaymentEntity.builder()
                .orderId(order.getOrderId())
                .productCost(productCost)
                .deliveryCost(deliveryCost)
                .totalCost(totalCost)
                .feeTotal(feeTotal)
                .state(PaymentState.PENDING)
                .build();

        PaymentEntity savedPayment = paymentRepository.save(payment);
        log.info("Платеж сохранен с идентификатором: {}, статус: PENDING", savedPayment.getPaymentId());

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public Double productCost(OrderDto order) {
        log.info("Расчет стоимости продукции для заказа: {}", order.getOrderId());

        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            log.warn("В заказе {} нет товаров",  order.getOrderId());
                throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров");
        }

        double totalProductCost = 0.0;

        List<UUID> productsIds = new ArrayList<>(order.getProducts().keySet());
        List<ProductDto> products = shoppingStoreClient.getProducts(productsIds);

        Map<UUID, ProductDto> productsMap = products.stream()
                .collect(Collectors.toMap(
                        ProductDto::getProductId,
                        Function.identity()
                ));

        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductDto product = productsMap.get(productId);

            if (product == null || product.getPrice() == null) {
                log.error("Товар не найден или не имеет цены: {}", productId);
                throw new NotEnoughInfoInOrderToCalculateException("Товар не найден или не имеет цены");
            }

            totalProductCost += product.getPrice() * quantity;
        }
        log.info("По заказу {} стоимость продукции: {}", order.getOrderId(), totalProductCost);
        return totalProductCost;
    }

    @Override
    public Double getTotalCost(OrderDto order) {
        log.info("Расчет итоговой стоимости заказа: {}", order.getOrderId());

        Double productCost = order.getProductPrice();
        if (productCost == null) {
            productCost = productCost(order);
        }

        Double feeTotal = productCost * TAX_RATE;
        Double deliveryCost = order.getDeliveryPrice();

        if (deliveryCost == null) {
            deliveryCost = 0.0;
        }

        Double totalCost = productCost + feeTotal + deliveryCost;

        log.info("По заказу {} итоговая стоимость: product={}, delivery={}, tax={}, total={}",
                order.getOrderId(), productCost, deliveryCost, feeTotal, totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info("Обработка успешного платежа: {}", paymentId);

        PaymentEntity payment = findPendingPaymentOrThrow(paymentId);

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.paymentSuccess(payment.getOrderId());
        log.info("Успешная оплата заказа: {}", payment.getOrderId());
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        log.info("Обработка неудачной попытки оплаты: {}", paymentId);

        PaymentEntity payment = findPendingPaymentOrThrow(paymentId);

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
        log.info("Сбой в оплате заказа: {}", payment.getOrderId());
    }

    private PaymentEntity findPendingPaymentOrThrow(UUID paymentId) {
        PaymentEntity payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> {
                    log.warn("Платеж не найден: {}", paymentId);
                    return new NoOrderFoundException("Платеж не найден: " + paymentId);
                });

        if (payment.getState() != PaymentState.PENDING) {
            log.warn("Платеж {} не находится в состоянии PENDING", paymentId);
            throw new IllegalStateException("Платеж не находится в состоянии PENDING");
        }
        return payment;
    }
}
