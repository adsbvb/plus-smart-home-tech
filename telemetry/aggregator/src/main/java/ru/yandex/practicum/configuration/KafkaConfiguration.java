package ru.yandex.practicum.configuration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @Bean
    public Producer<String, SensorsSnapshotAvro> kafkaProducer(KafkaProperties kafkaProperties) {

        Properties properties = kafkaProperties.getProducer().getProperties();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty("bootstrap.servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, properties.getProperty("key.serializer"));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, properties.getProperty("value.serializer"));
        return new KafkaProducer<>(props);
    }

    @Bean
    public Consumer<String, SensorEventAvro> kafkaConsumer(KafkaProperties kafkaProperties) {

        Properties properties = kafkaProperties.getConsumer().getProperties();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty("bootstrap.servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getProperty("group.id"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, properties.getProperty("key.deserializer"));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,properties.getProperty("value.deserializer"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getProperty("enable.auto.commit"));
        return new KafkaConsumer<>(props);
    }
}
