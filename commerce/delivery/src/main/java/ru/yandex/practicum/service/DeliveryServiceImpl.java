package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dal.DeliveryRepository;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShipToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.DeliveryEntity;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryRequest) {
        log.info("Планирование доставки заказа: {}", deliveryRequest.getOrderId());

        DeliveryEntity newDelivery = deliveryMapper.toEntity(deliveryRequest);

        newDelivery.setDeliveryState(DeliveryState.CREATED);
        DeliveryEntity savedDelivery = deliveryRepository.save(newDelivery);

        log.info("Доставка создана с идентификатором: {}", savedDelivery.getDeliveryId());
        return deliveryMapper.toDto(savedDelivery);
    }

    @Override
    @Transactional
    public void pickedDelivery(UUID orderId) {
        log.info("Получен запрос на доставку с указанием идентификатора доставки {}", orderId);

        DeliveryEntity delivery = getDelivery(orderId);

        if (delivery.getDeliveryState() != DeliveryState.CREATED) {
            log.warn("Необходим статус доставки CREATED. Текущий статус: {}",
                    delivery.getDeliveryState());
            throw new IllegalStateException("Необходим статус доставки CREATED. Текущий статус: "
                    + delivery.getDeliveryState());
        }

        warehouseClient.shippedToDelivery(ShipToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        log.info("Доставка заказа {}. В настоящее время статус IN_PROGRESS", orderId);
    }

    @Override
    @Transactional
    public void successfulDelivery(UUID orderId) {
        log.info("Начало обработки successfulDelivery для заказа: {}", orderId);

        DeliveryEntity delivery = getDelivery(orderId);

        if (delivery.getDeliveryState() != DeliveryState.IN_PROGRESS) {
            log.warn("Необходим статус доставки IN_PROGRESS. Текущий статус: {}",
                    delivery.getDeliveryState());
            throw new IllegalStateException("Необходим статус доставки IN_PROGRESS. Текущий статус: "
                    + delivery.getDeliveryState());
        }

        orderClient.delivery(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        log.info("Доставка заказа {} успешно завершена",  orderId);
    }

    @Override
    @Transactional
    public void failedDelivery(UUID orderId) {
        log.info("Начало обработки failedDelivery для заказа: {}", orderId);

        DeliveryEntity delivery = getDelivery(orderId);

        orderClient.deliveryFailed(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        log.info("Статус доставки успешно изменен на FAILED для заказа: {}", orderId);
    }

    @Override
    public Double deliveryCost(OrderDto orderDto) {
        log.info("Расчет стоимости доставки заказа: {}", orderDto.getOrderId());

        Double weight = orderDto.getDeliveryWeight();
        Double volume = orderDto.getDeliveryVolume();
        Boolean fragile = orderDto.getFragile();

        DeliveryEntity delivery = getDelivery(orderDto.getOrderId());

        AddressDto warehouseAddress = deliveryMapper.toAddressDto(delivery.getFromAddress());
        AddressDto deliveryAddress = deliveryMapper.toAddressDto(delivery.getToAddress());

        // Базовая стоимость
        double cost = 5.0;

        // Умножаем базовую стоимость на число, зависящее от адреса склада
        if (warehouseAddress.getStreet().contains("ADDRESS_2")) {
            cost += cost * 2;
        }

        // Если в заказе есть признак хрупкости
        if (fragile) {
            cost += cost * 0.2;
        }

        // Вес заказа, умноженный на 0.3
        cost += weight * 0.3;
        // Объём, умноженный на 0.2
        cost += volume * 0.2;

        // Учёт адреса доставки
        if (!deliveryAddress.getStreet().equals(warehouseAddress.getStreet())) {
            cost += cost * 0.2;
        }

        log.info("Итоговая стоимость доставки: {}", cost);
        return cost;
    }

    private DeliveryEntity getDelivery(UUID orderId) {
        return deliveryRepository.findDeliveryByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn("Доставка по идентификатору заказа {} не найдена", orderId);
                    return new NoDeliveryFoundException("Доставка по идентификатору заказа не найдена: "
                            + orderId);
                });
    }
}
