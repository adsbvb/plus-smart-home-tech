package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getClientOrders(
            @RequestParam String username
    ) {
        return orderService.getUserOrders(username);
    }

    @PutMapping
    public OrderDto createOrder(
            @RequestBody CreateNewOrderRequest request
    ) {
        return orderService.createOrder(request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(
            @RequestBody ProductReturnRequest request
    ) {
        return orderService.returnOrder(request);
    }

    @PostMapping("/payment")
    public OrderDto payment(
            @RequestBody UUID orderId
    ) {
        return orderService.paymentOrder(orderId);
    }

    @PostMapping("/paymnet/success")
    public OrderDto paymentSuccess(
            @RequestBody UUID orderId
    ) {
        return orderService.paymentSuccess(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(
            @RequestBody UUID orderId
    ) {
        return orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto delivery(
            @RequestBody UUID orderId
    ) {
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(
            @RequestBody UUID orderId
    ) {
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(
            @RequestBody UUID orderId
    ) {
        return orderService.completedOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(
            @RequestBody UUID orderId
    ) {
        return orderService.calculateTotal(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDelivery(
            @RequestBody UUID orderId
    ) {
        return orderService.calculateDelivery(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assembly(
            @RequestBody UUID orderId
    ) {
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(
            @RequestBody UUID orderId
    ) {
        return orderService.assemblyFailed(orderId);
    }
}