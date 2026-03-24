package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dal.WarehouseRepository;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProductEntity;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final ShoppingStoreClient shoppingStoreClient;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        UUID productId = request.getProductId();

        if (warehouseRepository.existsByProductId(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже есть на складе: " + productId);
        }

        WarehouseProductEntity entity = warehouseMapper.toEntity(request);
        warehouseRepository.save(entity);
        log.info("Продукт успешно добавлен: {}", productId);
    }

    @Override
    @Transactional
    public BookedProductsDto checkProductQuantityState(ShoppingCartDto shoppingCart) {
        Map<UUID, Long> products = shoppingCart.getProducts();

        if (products == null || products.isEmpty()) {
            return BookedProductsDto.builder()
                    .deliveryWeight(0.0)
                    .deliveryVolume(0.0)
                    .fragile(false)
                    .build();
        }

        List<UUID> productIds = new ArrayList<>(products.keySet());

        Map<UUID, WarehouseProductEntity> warehouseProducts = warehouseRepository
                .findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(
                        WarehouseProductEntity::getProductId,
                        entity -> entity
                ));

        List<String> errors = new ArrayList<>();
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long requestQuantity = entry.getValue();

            WarehouseProductEntity product = warehouseProducts.get(productId);

            if (product == null) {
                errors.add("Товар не найден на складе: " + productId);
                continue;
            }

            if (product.getQuantity() < requestQuantity) {
                errors.add("Количество товара id: " + productId + " Запрос: "
                        + requestQuantity + "Наличие: " + product.getQuantity());
            }

            totalWeight += (product.getWeight() != null ? product.getWeight() : 0.0) * requestQuantity;
            totalVolume += calculateVolume(product.getWidth(), product.getHeight(), product.getDepth())
                    * requestQuantity;
            hasFragile = hasFragile || Boolean.TRUE.equals(product.getFragile());
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.join("; ", errors);
            log.error("Недостаточное количество товара на складе: {}", errorMessage);
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточное количество товара на складе: "
                    + errorMessage);
        }

        log.info("Проверка склада пройдена. Общий вес: {}, объем {}, хрупкий товар: {}",
                totalWeight,  totalVolume, hasFragile);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        UUID productId = request.getProductId();

        WarehouseProductEntity product = warehouseRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар для добавления не найден: "
                        + productId));

        Long newQuantity = product.getQuantity() + request.getQuantity();
        product.setQuantity(newQuantity);
        warehouseRepository.save(product);

        log.info("Товар добавлен на склад: {}, количество: {}", productId, request.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    private Double calculateVolume(Double width, Double height, Double depth) {
        if (width == null || height == null || depth == null) {
            return 0.0;
        }
        return width * height * depth;
    }

    private QuantityState getWarehouseQuantityState(Long quantity) {
        if (quantity == null ||  quantity == 0) {
            return QuantityState.ENDED;
        }
        if (quantity < 10) {
            return QuantityState.FEW;
        }
        if (quantity <= 100) {
            return QuantityState.ENOUGH;
        }
        return QuantityState.MANY;
    }
}