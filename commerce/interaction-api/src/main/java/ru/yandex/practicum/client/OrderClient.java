package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @PostMapping("/delivery")
    OrderDto delivery(
            @RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(
            @RequestBody UUID orderId);

    @PostMapping("/payment/success")
    public OrderDto paymentSuccess(
            @RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(
            @RequestBody UUID orderId);
}
