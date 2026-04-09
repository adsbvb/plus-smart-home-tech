package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.WarehouseBookingRepository;
import ru.yandex.practicum.dal.WarehouseProductRepository;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.BookingNotFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseBookingEntity;
import ru.yandex.practicum.model.WarehouseProductEntity;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseBookingRepository warehouseBookingRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        UUID productId = request.getProductId();

        if (warehouseProductRepository.existsByProductId(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже есть на складе: " + productId);
        }

        WarehouseProductEntity entity = warehouseMapper.toEntity(request);
        warehouseProductRepository.save(entity);

        log.info("Продукт успешно добавлен: {}", productId);
    }

    @Override
    public BookedProductsDto checkProductQuantityState(ShoppingCartDto shoppingCart) {
        log.info("Проверка наличия товара на складе для корзины: {}", shoppingCart.getShoppingCartId());

        Map<UUID, Long> products = shoppingCart.getProducts();

        if (products == null || products.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("Получен пустой список продуктов");
        }

        List<UUID> productIds = new ArrayList<>(products.keySet());
        Map<UUID, WarehouseProductEntity> warehouseProducts = getWarehouseProducts(productIds);

        return checkProductQuantity(products, warehouseProducts);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        UUID productId = request.getProductId();

        WarehouseProductEntity product = warehouseProductRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар для добавления не найден: "
                        + productId));

        Long newQuantity = product.getQuantity() + request.getQuantity();
        product.setQuantity(newQuantity);
        warehouseProductRepository.save(product);

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

    @Override
    @Transactional
    public void shippedToDelivery(ShipToDeliveryRequest request) {
        log.info("Передача товаров в доставку для заказа: {}, deliveryId: {}",
                request.getOrderId(), request.getDeliveryId());

        List<WarehouseBookingEntity> bookings = warehouseBookingRepository.findByOrderId(request.getOrderId());

        if (bookings == null || bookings.isEmpty()) {
            log.warn("Не найдено бронирований для заказа: {}", request.getOrderId());
            throw new BookingNotFoundException("Не найдено бронирований для заказа");
        }

        for (WarehouseBookingEntity booking : bookings) {
            booking.setDeliveryId(request.getDeliveryId());
            booking.setShippedAt(LocalDateTime.now());
        }

        warehouseBookingRepository.saveAll(bookings);
        log.info("Товары для заказа {} переданы в доставку. Количество позиций: {}",
                request.getOrderId(), bookings.size());
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Long> products) {
        log.info("Прием товара на склад после возврата. Количество: {}", products.size());

        if (products.isEmpty()) {
            log.warn("Получен пустой список товаров на возврат");
            throw new IllegalArgumentException("Получен пустой список товаров на возврат");
        }

        List<UUID> productIds = new ArrayList<>(products.keySet());
        Map<UUID, WarehouseProductEntity> warehouseProducts = getWarehouseProducts(productIds);

        List<WarehouseProductEntity> productsToUpdate = new ArrayList<>();

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            WarehouseProductEntity product = warehouseProducts.get(productId);

            product.setQuantity(product.getQuantity() + quantity);
            productsToUpdate.add(product);
        }
        warehouseProductRepository.saveAll(productsToUpdate);

        log.info("Возврат товаров успешно обработан");
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductForOrder(AssemblyProductsForOrderRequest request) {
        log.info("Бронирование товаров на складе для заказа: {}", request.getOrderId());

        Map<UUID, Long> requestProducts = request.getProducts();

        if (requestProducts == null || requestProducts.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("Получен пустой список продуктов");
        }

        List<UUID> productIds = new ArrayList<>(requestProducts.keySet());
        Map<UUID, WarehouseProductEntity> warehouseProducts = getWarehouseProducts(productIds);

        BookedProductsDto bookedProductsDto = checkProductQuantity(requestProducts, warehouseProducts);

        List<WarehouseProductEntity> productsToUpdate = new ArrayList<>();
        List<WarehouseBookingEntity> bookings = new ArrayList<>();

        for (Map.Entry<UUID, Long> entry : requestProducts.entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();

            WarehouseProductEntity product = warehouseProducts.get(productId);

            product.setQuantity(product.getQuantity() - requestedQuantity);
            productsToUpdate.add(product);

            WarehouseBookingEntity booking = WarehouseBookingEntity.builder()
                    .orderId(request.getOrderId())
                    .productId(productId)
                    .quantity(requestedQuantity)
                    .bookedAt(LocalDateTime.now())
                    .build();
            bookings.add(booking);
        }

        warehouseProductRepository.saveAll(productsToUpdate);
        warehouseBookingRepository.saveAll(bookings);

        log.info("Обновлено {} товаров и создано {} бронирований для заказа {}",
                productsToUpdate.size(), bookings.size(), request.getOrderId());

        return bookedProductsDto;
    }

    private Double calculateVolume(Double width, Double height, Double depth) {
        if (width == null || height == null || depth == null) {
            return 0.0;
        }
        return width * height * depth;
    }

    private Map<UUID, WarehouseProductEntity> getWarehouseProducts(List<UUID> productIds) {
        return warehouseProductRepository
                .findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(
                        WarehouseProductEntity::getProductId,
                        entity -> entity
                ));
    }

    private BookedProductsDto checkProductQuantity(
            Map<UUID, Long> products,  Map<UUID, WarehouseProductEntity> warehouseProducts) {

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
}