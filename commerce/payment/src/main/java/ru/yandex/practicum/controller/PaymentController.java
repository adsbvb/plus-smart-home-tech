package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto payment(
            @Valid @RequestBody OrderDto orderDto
            ) {
        log.info("PaymentController: payment({})", orderDto);
        return paymentService.payment(orderDto);
    }

    @PostMapping("/totalCost")
    public Double getTotalCost(
            @Valid @RequestBody OrderDto orderDto
    ) {
        log.info("PaymentController: totalCost({})", orderDto);
        return paymentService.getTotalCost(orderDto);
    }

    @PostMapping("/refund")
    public void paymentSuccess(
            @RequestBody UUID paymentId
    ) {
        log.info("PaymentController: paymentSuccess({})", paymentId);
        paymentService.paymentSuccess(paymentId);
    }

    @PostMapping("/productCost")
    public Double productsCost(
            @Valid @RequestBody OrderDto orderDto
    ) {
        log.info("PaymentController: productsCost({})", orderDto);
        return paymentService.productCost(orderDto);
    }

    @PostMapping("/failed")
    public void paymentFailed(
            @RequestBody UUID paymentId
    ) {
        log.info("PaymentController: paymentFailed({})", paymentId);
        paymentService.paymentFailed(paymentId);
    }
}
