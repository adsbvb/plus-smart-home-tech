package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProductByCategory(ProductCategory category, Pageable pageable);

    ProductDto createProduct(@Valid ProductDto productDto);

    ProductDto updateProduct(@Valid ProductDto productDto);

    boolean deactivateProduct(UUID productId);

    boolean updateQuantityState(@Valid SetProductQuantityStateRequest request);

    ProductDto getProductById(UUID productId);
}