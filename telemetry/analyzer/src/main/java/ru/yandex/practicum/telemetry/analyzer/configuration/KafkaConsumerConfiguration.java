package ru.yandex.practicum.telemetry.analyzer.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfiguration {

    private final KafkaConfiguration configuration;

    public static final String SNAPSHOTS_TOPIC = "telemetry.snapshots.v1";
    public static final String HUBS_EVENTS_TOPIC = "telemetry.hubs.v1";


    @Bean
    public Consumer<String, SensorsSnapshotAvro> getSnapshotConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getSnapshotConfig().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, configuration.getSnapshotConfig().getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, configuration.getSnapshotConfig().getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, configuration.getSnapshotConfig().isEnableAutoCommit());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public Consumer<String, HubEventAvro> getHubConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getHubEventConfig().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, configuration.getHubEventConfig().getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, configuration.getHubEventConfig().getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, configuration.getHubEventConfig().isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, configuration.getHubEventConfig().getAutoCommitIntervalMs());
        return new KafkaConsumer<>(props);
    }
}