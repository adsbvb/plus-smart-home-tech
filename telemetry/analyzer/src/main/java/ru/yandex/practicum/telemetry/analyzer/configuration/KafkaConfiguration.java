package ru.yandex.practicum.telemetry.analyzer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaConfiguration {

    private String bootstrapServers;

    private SnapshotConfig snapshotConfig = new SnapshotConfig();
    private HubEventConfig hubEventConfig = new HubEventConfig();

    @Data
    public static class SnapshotConfig {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
        private boolean enableAutoCommit;
    }

    @Data
    public static class HubEventConfig {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
        private boolean enableAutoCommit;
        private String autoCommitIntervalMs;
    }
}
