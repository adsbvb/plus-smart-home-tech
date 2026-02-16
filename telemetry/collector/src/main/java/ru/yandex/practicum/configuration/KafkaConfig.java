package ru.yandex.practicum.configuration;

public class KafkaConfig {

    // topics
    public static final String SENSOR_EVENTS = "telemetry.sensors.v1";
    public static final String HUBS_EVENTS = "telemetry.hubs.v1";

    // configurations
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String KEY_SERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringSerializer";
    public static final String VALUE_SERIALIZER_CLASS =  "ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer";
}
