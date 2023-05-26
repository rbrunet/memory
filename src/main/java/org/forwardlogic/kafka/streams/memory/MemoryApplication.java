package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@SpringBootApplication
@EnableTransactionManagement
@EnableKafka
@EnableKafkaStreams
public class MemoryApplication {

	private static final String BOOTSTRAP_SERVERS = "localhost:9092";
	public static final String USED_MEMORY_TOPIC = "used-memory";


	@Value(value = "${spring.kafka.streams.state.dir}")
	private String stateStoreLocation;


	public static void main(String[] args) {
		SpringApplication.run(MemoryApplication.class, args);
	}

	@Bean("kafka-used-memory-producer")
	public KafkaProducer<String, UsedMemory> kafkaProducer() {
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UsedMemorySerde.class);

		KafkaProducer<String, UsedMemory> kafkaProducer = new KafkaProducer<String, UsedMemory>(producerProps);
		return kafkaProducer;
	}

	@Bean
	public KafkaTemplate<String, UsedMemory> bytesTemplate(ProducerFactory<String, UsedMemory> pf) {
		return new KafkaTemplate<>(pf,
				Collections.singletonMap(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UsedMemorySerde.class));
	}


	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	KafkaStreamsConfiguration kStreamsConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
		props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, UsedMemoryCountAndSumSerde.class.getCanonicalName());
		props.put(STATE_DIR_CONFIG, stateStoreLocation);
		return new KafkaStreamsConfiguration(props);
	}
}
