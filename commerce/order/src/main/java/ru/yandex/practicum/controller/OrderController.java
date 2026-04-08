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
        log.info("GET client orders: {}", username);
        return orderService.getUserOrders(username);
    }

    @PutMapping
    public OrderDto createOrder(
            @RequestBody CreateNewOrderRequest request
    ) {
        log.info("POST create order");
        return orderService.createOrder(request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(
            @RequestBody ProductReturnRequest request
    ) {
        log.info("POST return order");
        return orderService.returnOrder(request);
    }

    @PostMapping("/payment")
    public OrderDto payment(
            @RequestBody UUID orderId
    ) {
        log.info("POST payment order: {}", orderId);
        return orderService.paymentOrder(orderId);
    }

    @PostMapping("/paymnet/success")
    public OrderDto paymentSuccess(
            @RequestBody UUID orderId
    ) {
        log.info("POST payment success order: {}", orderId);
        return orderService.paymentSuccess(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(
            @RequestBody UUID orderId
    ) {
        log.info("POST payment failed order: {}", orderId);
        return orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto delivery(
            @RequestBody UUID orderId
    ) {
        log.info("POST delivery order: {}", orderId);
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/success")
    public OrderDto deliverySuccess(
            @RequestBody UUID orderId
    ) {
        log.info("POST delivery success order : {}", orderId);
        return orderService.deliverySuccess(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(
            @RequestBody UUID orderId
    ) {
        log.info("POST delivery failed order: {}", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto complete(
            @RequestBody UUID orderId
    ) {
        log.info("POST complete order: {}", orderId);
        return orderService.complete(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(
            @RequestBody UUID orderId
    ) {
        log.info("POST calculate total order: {}", orderId);
        return orderService.calculateTotal(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDelivery(
            @RequestBody UUID orderId
    ) {
        log.info("POST calculate delivery order: {}", orderId);
        return orderService.calculateDelivery(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assembly(
            @RequestBody UUID orderId
    ) {
        log.info("POST assembly order: {}", orderId);
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(
            @RequestBody UUID orderId
    ) {
        log.info("POST assembly failed order: {}", orderId);
        return orderService.assemblyFailed(orderId);
    }
}