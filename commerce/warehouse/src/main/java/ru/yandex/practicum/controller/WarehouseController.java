package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PutMapping
    public void newProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest request
    ) {
        log.info("WarehouseController::newProductInWarehouse");
        warehouseService.newProductInWarehouse(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityState(
            @Valid @RequestBody ShoppingCartDto shoppingCart
    ) {
        log.info("WarehouseController::checkProductQuantityState");
        return warehouseService.checkProductQuantityState(shoppingCart);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest request
    ) {
        log.info("WarehouseController::addProductToWarehouse");
        warehouseService.addProductToWarehouse(request);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("WarehouseController::getWarehouseAddress");
        return warehouseService.getWarehouseAddress();
    }

    @PostMapping("/shipped")
    public void shippedToDelivery(
            @Valid @RequestBody ShipToDeliveryRequest request
    ) {
        log.info("WarehouseController::shippedToDelivery");
        warehouseService.shippedToDelivery(request);
    }

    @PostMapping("/return")
    public void acceptReturn(
            @RequestBody Map<UUID, Long> products
    ) {
        log.info("WarehouseController::acceptReturn");
        warehouseService.acceptReturn(products);
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(
            @Valid @RequestBody AssemblyProductsForOrderRequest request
    ) {
        log.info("WarehouseController::assemblyOrder");
        return warehouseService.assemblyProductForOrder(request);
    }
}
