package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
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
        if (username == null || username.isEmpty()) {
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
        BookedProductsDto bookedProducts =
                warehouseClient.checkProductQuantityState(request.getShoppingCartDto());

        OrderEntity createdOrder = OrderEntity.builder()
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
                        .order(createdOrder)
                        .productId(entry.getKey())
                        .quantity(entry.getValue())
                        .build())
                .toList();

        createdOrder.setProducts(orderProducts);
        OrderEntity savedOrder = orderRepository.save(createdOrder);

        warehouseClient.assemblyProductsForOrder(AssemblyProductsForOrderRequest.builder()
                .products(requestProducts)
                .orderId(savedOrder.getOrderId())
                .build());

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
    public OrderDto deliveryOrder(UUID orderId) {
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
    public OrderDto orderAssembly(UUID orderId) {
        OrderEntity order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден: " + orderId));

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
