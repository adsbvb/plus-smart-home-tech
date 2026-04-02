package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.DeliveryRepository;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.DeliveryEntity;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryRequest) {
        DeliveryEntity newDelivery = deliveryMapper.toEntity(deliveryRequest);
        newDelivery.setDeliveryState(DeliveryState.CREATED);
        DeliveryEntity savedDelivery = deliveryRepository.save(newDelivery);
        return deliveryMapper.toDto(savedDelivery);
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        DeliveryEntity delivery = deliveryRepository.findDeliveryByOrderId(orderId);

        if (delivery.getDeliveryState() != DeliveryState.CREATED) {
            throw new NoDeliveryFoundException("Забрать заказ можно только в статусе CREATED");
        }
    }

    @Override
    public void pickedDelivery(UUID orderId) {

    }

    @Override
    public void failedDelivery(UUID orderId) {

    }

    @Override
    public Double costDelivery(OrderDto orderDto) {
        return 0.0;
    }


}
