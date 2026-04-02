package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryRequest) {
        log.info("Планирование доставки заказа: {}", deliveryRequest.getOrderId());

        DeliveryEntity newDelivery = deliveryMapper.toEntity(deliveryRequest);

        newDelivery.setDeliveryState(DeliveryState.CREATED);
        DeliveryEntity savedDelivery = deliveryRepository.save(newDelivery);

        log.info("Доставка создана с идентификатором: {}", savedDelivery.getDeliveryId());
        return deliveryMapper.toDto(savedDelivery);
    }

    @Override
    public void pickedDelivery(UUID orderId) {
        log.info("Получен запрос на доставку с указанием идентификатора доставки {}", orderId);

        DeliveryEntity delivery = getDelivery(orderId);

        if (delivery.getDeliveryState() != DeliveryState.CREATED) {
            log.warn("Забрать заказ можно только в статусе CREATED. Текущий статус: {}",
                    delivery.getDeliveryState());
            throw new NoDeliveryFoundException("Забрать заказ можно только в статусе CREATED");
        }

        warehouseClient.shipToDelivery(ShipToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        log.info("Доставка заказа {} в настоящее время находится IN_PROGRESS", orderId);
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        log.info("Начало обработки successfulDelivery для заказа: {}", orderId);

        DeliveryEntity delivery = getDelivery(orderId);

        if (delivery.getDeliveryState() != DeliveryState.IN_PROGRESS) {
            log.warn("Только доставка в статусе IN_PROGRESS может быть отмечена как успешная. Текущий статус: {}",
                    delivery.getDeliveryState());
            throw new NoDeliveryFoundException("Только доставка в статусе IN_PROGRESS может быть отмечена как успешная");
        }

        orderClient.deliveryOrder(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        log.info("Доставка заказа {} успешно завершена",  orderId);
    }

    @Override
    public void failedDelivery(UUID orderId) {
        log.info("Начало обработки failedDelivery для заказа: {}", orderId);

        DeliveryEntity delivery = getDelivery(orderId);

        orderClient.deliveryFailed(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        log.info("Статус доставки успешно изменен на FAILED для заказа: {}", orderId);
    }

    @Override
    public Double costDelivery(OrderDto orderDto) {
        log.info("Расчет стоимости доставки заказа: {}", orderDto.getOrderId());

        Double weight = orderDto.getDeliveryWeight();
        Double volume = orderDto.getDeliveryVolume();
        Boolean fragile = orderDto.getFragile();

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        AddressDto deliveryAddress = warehouseClient.getWarehouseAddress(); // !!!!!!!!!!!!!

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
