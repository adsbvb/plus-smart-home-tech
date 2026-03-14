package ru.yandex.practicum.telemetry.analyzer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootstrapServers;

    private SnapshotConfig snapshotConfig = new SnapshotConfig();
    private HubEventConfig hubEventConfig = new HubEventConfig();

    @Data
    public static class SnapshotConfig {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
        private boolean enableAutoCommit;
        private List<String> topics;
        private Duration pollTimeout = Duration.ofMillis(100);
    }

    @Data
    public static class HubEventConfig {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
        private boolean enableAutoCommit;
        private String autoCommitIntervalMs;
        private List<String> topics;
        private Duration pollTimeout = Duration.ofMillis(100);
    }
}
