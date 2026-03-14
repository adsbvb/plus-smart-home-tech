package ru.yandex.practicum.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "collector.kafka")
public class KafkaProperties {
    private String bootstrapServers;

    private String topicSensor;
    private String topicHub;

    private Producer producer;

    @Getter
    @Setter
    public static class Producer {
        private Properties properties;
    }
}
