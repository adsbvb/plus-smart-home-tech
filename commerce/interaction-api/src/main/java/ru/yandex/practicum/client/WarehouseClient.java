package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.*;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityState(
            @Valid @RequestBody ShoppingCartDto shoppingCart);

    @GetMapping
    AddressDto getWarehouseAddress();

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    void shipToDelivery(
            @Valid @RequestBody ShipToDeliveryRequest request);
}
