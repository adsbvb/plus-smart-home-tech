package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.CartEntity;
import ru.yandex.practicum.model.CartProductEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "shoppingCartId", source = "cartId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productsToMap")
    ShoppingCartDto toDto(CartEntity cart);

    @Named("productsToMap")
    default Map<UUID,Long> productsToMap(List<CartProductEntity> products) {
        if (products == null) {
            return Map.of();
        }
        return products.stream()
                .filter(p -> p.getProductId() != null && p.getQuantity() != null)
                .collect(Collectors.toMap(
                        CartProductEntity::getProductId,
                        CartProductEntity::getQuantity,
                        Long::sum
                ));
    }
}
