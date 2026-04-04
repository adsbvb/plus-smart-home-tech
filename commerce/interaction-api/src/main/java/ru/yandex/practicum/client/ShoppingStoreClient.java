package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId);

    @GetMapping
    Page<ProductDto> getProductsByCategory(
            @RequestParam("category") ProductCategory category,
            @PageableDefault(page = 0, size = 20, sort = "productName") Pageable pageable);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam QuantityState quantityState
    );
}
