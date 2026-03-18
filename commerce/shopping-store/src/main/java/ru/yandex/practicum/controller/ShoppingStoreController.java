package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.service.ProductService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductDto> getProducts(
            @RequestParam("category") ProductCategory category,
            @PageableDefault(page = 0, size = 20, sort = "productName")Pageable pageable
    ) {
        return productService.getProductByCategory(category, pageable);
    }

    @PutMapping
    public ProductDto createNewProduct(
            @Valid @RequestBody ProductDto productDto
    ) {
        return productService.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(
            @Valid @RequestBody ProductDto productDto
    ) {
        return productService.updateProduct(productDto);
    }

    @PostMapping("/{removeProductFromStore}")
    boolean removeProductFromStore(
            @RequestBody UUID productId
    ) {
        return productService.deactivateProduct(productId);
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(
            @Valid @RequestBody SetProductQuantityStateRequest request
    ) {
        return productService.updateQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(
            @PathVariable("productId") UUID productId
    ) {
        return productService.getProductById(productId);
    }

}
