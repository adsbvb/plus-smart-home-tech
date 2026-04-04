package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getUserOrders(String username);

    OrderDto createOrder(CreateNewOrderRequest request);

    OrderDto returnOrder(ProductReturnRequest request);

    OrderDto paymentOrder(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto delivery(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto calculateTotal(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto assembly(UUID orderId);

    OrderDto orderAssemblyFailed(UUID orderId);
}