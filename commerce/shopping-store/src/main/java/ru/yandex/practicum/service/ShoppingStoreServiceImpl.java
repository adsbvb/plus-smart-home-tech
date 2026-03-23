package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.ProductRepository;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.ProductEntity;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductDto> getProductByCategory(ProductCategory category, Pageable pageable) {
        return productRepository
                //.findByProductCategoryAndProductState(category, ProductState.ACTIVE, pageable)
                .findByProductCategory(category, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        ProductEntity product = productMapper.toEntity(productDto);
        ProductEntity savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        if (productId == null) {
            throw new IllegalArgumentException("ProductId должен быть указан");
        }

        ProductEntity existingProduct = productRepository
                .findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Продукт не найден с id: " + productId
                ));

        productMapper.updateEntity(productDto, existingProduct);

        ProductEntity updatedProduct = productRepository.save(existingProduct);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public boolean deactivateProduct(UUID productId) {
        if (!existProduct(productId)) {
            throw new ProductNotFoundException("Продукт не найден с id: " + productId);
        }
        int countUpdate = productRepository.deactivateProduct(productId);
        return countUpdate > 0;
    }

    @Override
    @Transactional
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        if (!existProduct(request.getProductId())) {
            throw new ProductNotFoundException("Продукт не найден с id: " + request.getProductId());
        }
        int countUpdate = productRepository.updateProductQuantity(request.getProductId(), request.getQuantityState());
        return countUpdate > 0;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        ProductEntity product = productRepository
                //.findByProductIdAndProductState(productId, ProductState.ACTIVE)
                .findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Продукт не найден с id: " + productId));
        return  productMapper.toDto(product);
    }

    private boolean existProduct(UUID productId) {
        return productRepository.existsByProductIdAndProductState(productId, ProductState.ACTIVE);
    }
}
