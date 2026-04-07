package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.dto.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void newProductInWarehouse(@Valid NewProductInWarehouseRequest request);

    BookedProductsDto checkProductQuantityState(@Valid ShoppingCartDto shoppingCart);

    void addProductToWarehouse(@Valid AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    void shippedToDelivery(@Valid ShipToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> products);

    BookedProductsDto assemblyProductForOrder(@Valid AssemblyProductsForOrderRequest request);
}
