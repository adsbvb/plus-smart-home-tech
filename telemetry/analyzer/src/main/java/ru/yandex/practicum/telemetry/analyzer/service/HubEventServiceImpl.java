package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.service.handler.hub.HubEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubEventServiceImpl implements HubEventService {

    private final Map<String, HubEventHandler> hubEventHandlers;

    public HubEventServiceImpl(Set<HubEventHandler> handlers) {
        this.hubEventHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()
                ));
    }

    @Override
    public void processEvent(HubEventAvro event) {
        String type = event.getPayload().getClass().getName();

        HubEventHandler handler = hubEventHandlers.get(type);

        if (handler == null) {
            log.warn("Нет обработчика для типа события: {}", type);
            return;
        }

        handler.handle(event);
    }
}
