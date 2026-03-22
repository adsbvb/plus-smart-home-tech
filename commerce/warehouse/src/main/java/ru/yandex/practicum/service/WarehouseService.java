package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.dto.*;

public interface WarehouseService {
    void newProductInWarehouse(@Valid NewProductInWarehouseRequest request);

    BookedProductsDto checkProductQuantityState(@Valid ShoppingCartDto shoppingCart);

    void addProductToWarehouse(@Valid AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
