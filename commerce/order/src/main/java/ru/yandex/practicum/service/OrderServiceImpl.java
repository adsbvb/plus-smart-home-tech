package ru.yandex.practicum.service;

import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.OrderRepository;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.OrderEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

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
