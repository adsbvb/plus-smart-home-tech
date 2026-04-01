package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    @PutMapping
    public DeliveryDto planDelivery(
            @RequestBody DeliveryDto deliveryDto
    ) {
        return null;
    }

    @PostMapping("/successful")
    public void successfulDelivery(
            @RequestBody UUID orderId
    ) {

    }

    @PostMapping("/picked")
    public void pickedDelivery(
            @RequestBody UUID orderId
    ) {

    }

    @PostMapping("/failed")
    public void failedDelivery(
            @RequestBody UUID orderId
    ) {

    }

    @PostMapping("/cost")
    public Double costDelivery(
            @Valid @RequestBody OrderDto orderDto
    ) {
        return null;
    }


}
