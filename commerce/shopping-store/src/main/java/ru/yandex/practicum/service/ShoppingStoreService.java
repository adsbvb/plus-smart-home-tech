package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    Page<ProductDto> getProductByCategory(ProductCategory category, Pageable pageable);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean deactivateProduct(UUID productId);

    boolean updateQuantityState(SetProductQuantityStateRequest request);

    ProductDto getProductById(UUID productId);

    List<ProductDto> getProductsBatch(List<UUID> productIds);
}