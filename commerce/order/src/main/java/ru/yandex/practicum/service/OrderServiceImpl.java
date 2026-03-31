package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    @Override
    public List<OrderDto> getUserOrders(String username) {
        return List.of();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        return null;
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
        return null;
    }

    @Override
    public OrderDto orderAssemblyFailed(UUID orderId) {
        return null;
    }
}
