package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaProperties;
import ru.yandex.practicum.telemetry.analyzer.service.HubEventService;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;
    private final HubEventService hubEventService;

    private final KafkaProperties kafkaProperties;

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Получен сигнал завершения, инициируем остановку...");
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(kafkaProperties.getHubEventConfig().getTopics());

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(
                        kafkaProperties.getHubEventConfig().getPollTimeout());

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    log.debug("Обработка хаб-ивента: topic={}, partition={}, offset={}, hubId={}",
                            record.topic(), record.partition(), record.offset(), record.key());

                    handleHubEvent(record);
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Критическая ошибка", e);

        } finally {
            try {
                consumer.close();
                log.info("Consumer закрыт");
            } catch (Exception e) {
                log.error("Ошибка при закрытии consumer", e);
            }
        }
    }

    private void handleHubEvent(ConsumerRecord<String, HubEventAvro> record) {
        try {
            HubEventAvro event = record.value();
            hubEventService.processEvent(event);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения offset={}: {}" , record.offset(), e.getMessage(), e);
        }
    }

}