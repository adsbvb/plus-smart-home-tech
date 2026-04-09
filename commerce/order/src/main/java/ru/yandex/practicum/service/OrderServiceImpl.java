package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dal.OrderRepository;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.OrderEntity;
import ru.yandex.practicum.model.OrderProductEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getUserOrders(String username) {
        log.info("Получение заказов для пользователя: {}", username);

        if (username == null || username.isEmpty()) {
            log.warn("Некорректное имя пользователя: {}", username);
            throw new NotAuthorizedUserException("Некорректное имя пользователя");
        }

        List<OrderEntity> orders = orderRepository.findAllByUsername(username);

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа для пользователя: {}", request.getUsername());

        BookedProductsDto bookedProducts =
                warehouseClient.checkProductQuantityState(request.getShoppingCartDto());

        OrderEntity order = createOrderEntity(request, bookedProducts);
        List<OrderProductEntity> orderProducts = getOrderProducts(request, order);

        order.setProducts(orderProducts);
        orderRepository.save(order);

        DeliveryDto deliveryRequest = createDeliveryDto(order.getOrderId(), request.getDeliveryAddress());
        DeliveryDto delivery = deliveryClient.planDelivery(deliveryRequest);

        order.setDeliveryId(delivery.getDeliveryId());
        orderRepository.save(order);

        log.info("Заказ создан с идентификатором: {}", order.getOrderId());

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto paymentOrder(UUID orderId) {
        log.info("Обработка платежа по заказу:  {}", orderId);

        OrderEntity order = getOrderEntity(orderId);

        if (order.getState() != OrderState.ASSEMBLED) {
            log.warn("Заказ {} не находится в статусе ASSEMBLED. Текущий статус: {}",
                    orderId, order.getState());
            throw new IllegalStateException("Заказ не находится в статусе ASSEMBLED. Текущий статус: "
                    + order.getState());
        }

        OrderDto orderDto = orderMapper.toDto(order);

        Double deliveryPrice = deliveryClient.deliveryCost(orderDto);
        Double productPrice = paymentClient.productsCost(orderDto);

        orderDto.setDeliveryPrice(deliveryPrice);
        orderDto.setProductPrice(productPrice);

        PaymentDto payment = paymentClient.payment(orderDto);

        order.setPaymentId(payment.getPaymentId());
        order.setProductPrice(productPrice);
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(payment.getTotalPayment());
        order.setState(OrderState.ON_PAYMENT);

        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Инициирован платеж по заказу: {}", savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto paymentSuccess(UUID orderId) {
        log.info("Получено уведомление об успешной оплате заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);

        if (order.getState() != OrderState.ON_PAYMENT) {
            log.warn("Заказ {} не находится в статусе ON_PAYMENT. Текущий статус: {}",
                    orderId, order.getState());

            if (order.getState() == OrderState.PAID) {
                return orderMapper.toDto(order);
            }

            throw new IllegalStateException("Заказ не находится в статусе ON_PAYMENT. Текущий статус: "
                    + order.getState());
        }

        order.setState(OrderState.PAID);
        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Изменение статуса заказа {}. Текущий статус: {}", savedOrder.getOrderId(), savedOrder.getState());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Получено уведомление о неудачной оплате заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Передача заказа {} в службу доставки",  orderId);

        OrderEntity order = getOrderEntity(orderId);

        if (order.getState() != OrderState.PAID) {
            log.warn("Доставка невозможна. Заказ находиться в статусе: {}", order.getState());
            throw new IllegalStateException("Доставка невозможна. Заказ находиться в статуск: " + order.getState());
        }

        deliveryClient.pickedDelivery(orderId);

        order.setState(OrderState.ON_DELIVERY);
        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Статус заказа {} изменен на {}",  savedOrder.getOrderId(), savedOrder.getState());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto deliverySuccess(UUID orderId) {
        log.info("Получено уведомление об удачной доставке заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);

        if (order.getState() != OrderState.ON_DELIVERY) {
            log.warn("Заказ {} не находится в статусе ON_DELIVERY. Текущий статус: {}",
                    orderId, order.getState());
            throw new IllegalStateException("Заказ не находится в статусе ON_DELIVERY. Текущий статус: "
                    + order.getState());
        }

        order.setState(OrderState.DELIVERED);
        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Заказ {} успешно доставлен", savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Получено уведомление о неудачной доставке заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);

        if (order.getState() != OrderState.PAID && order.getState() != OrderState.DELIVERED) {
            log.warn("Заказ {} не может быть завершен в статусе {}",
                    orderId, order.getState());
            throw new IllegalStateException("Заказ не может быть завершен в статусе " + order.getState());
        }

        order.setState(OrderState.COMPLETED);
        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Заказ {} успешно завершен", savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Оформление возврата заказа: {}", request.getOrderId());

        OrderEntity order = getOrderEntity(request.getOrderId());

        warehouseClient.acceptReturn(request.getProducts());

        order.setState(OrderState.PRODUCT_RETURNED);
        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Статус заказа {} изменен на {}", savedOrder.getOrderId(), savedOrder.getState());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        log.info("Запрос для получения итоговой суммы заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);
        OrderDto orderDto = orderMapper.toDto(order);

        Double totalCost = paymentClient.getTotalCost(orderDto);
        orderDto.setTotalPrice(totalCost);

        return orderDto;
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        log.info("Запрос на получения стоимости доставки для заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);
        OrderDto orderDto = orderMapper.toDto(order);

        Double deliveryPrice = deliveryClient.deliveryCost(orderDto);
        orderDto.setDeliveryPrice(deliveryPrice);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Сборка заказа: {}", orderId);

        OrderEntity order = getOrderEntity(orderId);

        if (order.getState() != OrderState.NEW) {
            log.warn("Заказ не может быть собран в состоянии: {}", order.getState());
            throw new IllegalStateException("Заказ не может быть собран в состоянии: " + order.getState());
        }

        Map<UUID, Long> products = orderMapper.productsToMap(order.getProducts());

        warehouseClient.assemblyProductForOrderFromShoppingCart(
                AssemblyProductsForOrderRequest.builder()
                .orderId(orderId)
                .products(products)
                .build());

        log.info("Товар забронирован для заказа: {}", order.getOrderId());

        order.setState(OrderState.ASSEMBLED);
        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Заказ {} успешно собран",  savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Сборка заказа {} не удалась",  orderId);

        OrderEntity order = getOrderEntity(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    private OrderEntity getOrderEntity(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn("Заказ не найден: {}", orderId);
                    return new NoOrderFoundException("Заказ не найден: " + orderId);
                });
    }

    private OrderEntity createOrderEntity(
            CreateNewOrderRequest request, BookedProductsDto bookedProducts
    ) {
        return OrderEntity.builder()
                .username(request.getUsername())
                .shoppingCartId(request.getShoppingCartDto().getShoppingCartId())
                .state(OrderState.NEW)
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .fragile(bookedProducts.getFragile())
                .build();
    }

    private List<OrderProductEntity> getOrderProducts(
            CreateNewOrderRequest request, OrderEntity order
    ) {
        Map<UUID, Long> requestProducts = request.getShoppingCartDto().getProducts();
        return requestProducts.entrySet().stream()
                .map(entry -> OrderProductEntity.builder()
                        .order(order)
                        .productId(entry.getKey())
                        .quantity(entry.getValue())
                        .build())
                .toList();
    }

    private DeliveryDto createDeliveryDto(UUID orderId, AddressDto addressDto) {
        return DeliveryDto.builder()
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(addressDto)
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build();
    }
}
