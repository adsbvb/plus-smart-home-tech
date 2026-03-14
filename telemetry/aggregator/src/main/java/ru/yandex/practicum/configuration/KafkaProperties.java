package ru.yandex.practicum.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private Producer producer;
    private Consumer consumer;

    @Getter
    @Setter
    public static class Producer {
        private Properties properties;
        private String topic;
    }

    @Getter
    @Setter
    public static class Consumer {
        private Properties properties;
        private List<String> topics;
        private Duration pollTimeout =  Duration.ofMillis(100);
    }
}
