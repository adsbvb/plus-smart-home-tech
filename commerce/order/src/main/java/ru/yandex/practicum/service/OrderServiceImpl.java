package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.DeliveryClient;
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
    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа для пользователя: {}", request.getUsername());

        BookedProductsDto bookedProducts =
                warehouseClient.checkProductQuantityState(request.getShoppingCartDto());

        OrderEntity order = OrderEntity.builder()
                .username(request.getUsername())
                .shoppingCartId(request.getShoppingCartDto().getShoppingCartId())
                .state(OrderState.NEW)
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .fragile(bookedProducts.getFragile())
                .build();

        Map<UUID, Long> requestProducts = request.getShoppingCartDto().getProducts();
        List<OrderProductEntity> orderProducts = requestProducts.entrySet().stream()
                .map(entry -> OrderProductEntity.builder()
                        .order(order)
                        .productId(entry.getKey())
                        .quantity(entry.getValue())
                        .build())
                .toList();

        order.setProducts(orderProducts);

        OrderEntity savedOrder = orderRepository.save(order);
        log.info("Заказ создан с идентификатором: {}", savedOrder.getOrderId());

        warehouseClient.assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest.builder()
                .products(requestProducts)
                .orderId(savedOrder.getOrderId())
                .build());
        log.info("Товар забронирован для заказа: {}", savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        return null;
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Сборка заказа: {}", orderId);

        OrderEntity order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn("Заказ не найден: {}", orderId);
                    return new NoOrderFoundException("Заказ не найден: " + orderId);
                });

        if (order.getState() != OrderState.NEW && order.getState() != OrderState.ON_PAYMENT) {
            log.warn("Заказ не может быть собран в состоянии: {}", order.getState());
            throw new IllegalStateException("Заказ не может быть собран в состоянии: " + order.getState());
        }

        AddressDto addressDto = warehouseClient.getWarehouseAddress();


        order.setState(OrderState.ASSEMBLED);

        return null;
    }

    @Override
    public OrderDto orderAssemblyFailed(UUID orderId) {
        return null;
    }

    private DeliveryDto getDelivery(UUID orderId, AddressDto addressDto) {
        DeliveryDto delivery = DeliveryDto.builder()
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(addressDto)
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build();
        return deliveryClient.planDelivery(delivery);
    }
}
