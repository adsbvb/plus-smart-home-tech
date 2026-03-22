package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dal.CartProductRepository;
import ru.yandex.practicum.dal.CartRepository;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.CartEntity;
import ru.yandex.practicum.model.CartProductEntity;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final CartMapper cartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);

        CartEntity cart = cartRepository.findByUsername(username)
                .orElseGet(() -> createNewCart(username));

        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        validateUsername(username);

        CartEntity cart = cartRepository.findByUsernameAndIsActiveTrue(username)
                .orElseGet(() -> createNewCart(username));
        CartEntity newCart = cartRepository.save(cart);
        ShoppingCartDto checkoutCart = cartMapper.toDto(newCart);

        Map<UUID, Long> allProducts = checkoutCart.getProducts();
        if (allProducts == null) {
            allProducts = new HashMap<>();
            checkoutCart.setProducts(allProducts);
        }

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            Long currentQuantity = allProducts.getOrDefault(entry.getKey(), 0L);
            allProducts.put(entry.getKey(), currentQuantity + entry.getValue());
        }
        checkoutCart.setProducts(allProducts);

        log.info("Запрос на склад - shoppingCartId: {}, products: {}",
                checkoutCart.getShoppingCartId(),
                checkoutCart.getProducts());

        try {
            warehouseClient.checkProductQuantityState(checkoutCart);
        } catch (Exception e) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(e.getMessage());
        }

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            addOrUpdateCartProduct(newCart, entry.getKey(), entry.getValue());
        }

        CartEntity savedCart = cartRepository.save(newCart);
        log.info("Продукты успешно добавлены в корзину: {}", savedCart.getCartId());
        log.info("Количество продуктов в корзине после сохранения: {}", savedCart.getProducts().size());

        return cartMapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        validateUsername(username);

        int result = cartRepository.deactivateCart(username);
        if (result > 0) {
            log.info("Корзина успешно деактивирована для пользователя: " + username);
        } else {
            log.info("Не найдено активных корзин для пользователя: " + username);
        }
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> products) {
        validateUsername(username);

        CartEntity cart = cartRepository.findByUsernameAndIsActiveTrue(username).
                orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Не найдено активных корзин для пользователя: " + username));

        int result = cartProductRepository.deleteByCartIdAndProductIds(cart.getCartId(), products);
        log.info("Удалено продуктов: {}", result);

        cart = cartRepository.findByCartId(cart.getCartId()).orElse(cart);

        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);

        CartEntity cart = cartRepository.findByUsernameAndIsActiveTrue(username).
                orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Не найдено активных корзин для пользователя: " + username));

        CartProductEntity cartProduct = cartProductRepository
                .findByCart_CartIdAndProductId(cart.getCartId(), request.getProductId())
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Продукт не найден с id: " + request.getProductId()
                ));

        cartProduct.setQuantity(request.getNewQuantity());
        cartProductRepository.save(cartProduct);

        cart = cartRepository.findByCartId(cart.getCartId()).orElse(cart);
        log.info("Количество успешно обновлено");

        return cartMapper.toDto(cart);
    }

    private CartEntity createNewCart(String username) {
        return CartEntity.builder()
                .username(username)
                .isActive(true)
                .build();
    }

    private void addOrUpdateCartProduct(CartEntity cart, UUID productId, Long quantity) {
            cart.getProducts().stream()
                    .filter(product -> product.getProductId().equals(productId))
                    .findFirst()
                    .ifPresentOrElse(
                            product -> product.setQuantity(product.getQuantity() + quantity),
                            () -> {
                                CartProductEntity newCartProduct = CartProductEntity.builder()
                                        .cart(cart)
                                        .productId(productId)
                                        .quantity(quantity)
                                        .build();
                                cart.getProducts().add(newCartProduct);
                            }
                    );
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}
