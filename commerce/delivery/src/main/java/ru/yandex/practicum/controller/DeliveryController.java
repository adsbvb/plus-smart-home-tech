package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto planDelivery(
            @RequestBody DeliveryDto deliveryDto
    ) {
        log.info("Delivery Controller: planDelivery({})", deliveryDto);
        return deliveryService.planDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void successfulDelivery(
            @RequestBody UUID orderId
    ) {
        log.info("Delivery Controller: successfulDelivery({})", orderId);
        deliveryService.successfulDelivery(orderId);
    }

    @PostMapping("/picked")
    public void pickedDelivery(
            @RequestBody UUID orderId
    ) {
        log.info("Delivery Controller: pickedDelivery({})", orderId);
        deliveryService.pickedDelivery(orderId);
    }

    @PostMapping("/failed")
    public void failedDelivery(
            @RequestBody UUID orderId
    ) {
        log.info("Delivery Controller: failedDelivery({})", orderId);
        deliveryService.failedDelivery(orderId);
    }

    @PostMapping("/cost")
    public Double deliveryCost(
            @Valid @RequestBody OrderDto orderDto
    ) {
        log.info("Delivery Controller: costDelivery({})", orderDto);
        return deliveryService.deliveryCost(orderDto);
    }


}
