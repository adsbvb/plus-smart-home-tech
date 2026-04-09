package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.model.AddressEntity;
import ru.yandex.practicum.model.DeliveryEntity;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    AddressEntity toAddressEntity(AddressDto addressDto);
    AddressDto toAddressDto(AddressEntity addressEntity);

    DeliveryDto toDto(DeliveryEntity delivery);

    DeliveryEntity toEntity(DeliveryDto deliveryDto);
}
