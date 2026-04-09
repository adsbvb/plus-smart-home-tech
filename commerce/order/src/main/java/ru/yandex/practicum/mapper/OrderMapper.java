package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.OrderEntity;
import ru.yandex.practicum.model.OrderProductEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "products", source = "products", qualifiedByName = "productsToMap")
    OrderDto toDto(OrderEntity order);

    @Named("productsToMap")
    default Map<UUID, Long> productsToMap(List<OrderProductEntity> products) {
        if (products == null) {
            return Map.of();
        }
        return products.stream()
                .filter(p -> p.getProductId() != null && p.getQuantity() != null)
                .collect(Collectors.toMap(
                        OrderProductEntity::getProductId,
                        OrderProductEntity::getQuantity,
                        Long::sum
                ));
    }
}
