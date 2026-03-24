package ru.yandex.practicum.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class WarehouseClientFallback {

    private final WarehouseClient warehouseClient;

    @CircuitBreaker(name = "warehouse", fallbackMethod = "checkProductQuantityFallback")
    public BookedProductsDto checkProductQuantityState(ShoppingCartDto shoppingCart) {
        return warehouseClient.checkProductQuantityState(shoppingCart);
    }

    private BookedProductsDto checkProductQuantityFallback(
            ShoppingCartDto shoppingCart, Throwable t) {

        log.error("Warehouse service недоступен. Error: {}",
                t.getMessage());

        throw new RuntimeException("Сервис временно не доступен");
    }
}
