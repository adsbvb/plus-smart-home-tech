package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.model.PaymentEntity;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "totalPayment", source = "totalCost")
    @Mapping(target = "deliveryPayment", source = "deliveryCost")
    PaymentDto toDto(PaymentEntity payment);
}
