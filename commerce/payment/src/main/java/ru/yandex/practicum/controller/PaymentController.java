package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    @PostMapping
    public PaymentDto createPaymentForOrder(
            @Valid @RequestBody OrderDto orderDto
            ) {
        return null;
    }

    @PostMapping("/totalCost")
    public Double getTotalCostOrder(
            @Valid @RequestBody OrderDto orderDto
    ) {
        return null;
    }

    @PostMapping("/refund")
    public void successfulPayment(
            @RequestBody UUID orderId
    ) {

    }

    @PostMapping("/productCost")
    public Double getCostProductsForOrder(
            @Valid @RequestBody OrderDto orderDto
    ) {
        return null;
    }

    @PostMapping("/failed")
    public void failedPayment(
            @RequestBody UUID orderId
    ) {

    }
}
