package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.TimeWindowedDeserializer;
import org.apache.kafka.streams.kstream.TimeWindowedSerializer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.streams.StreamsConfig.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class MemoryFilterProcessorTest {
    private Properties props;
    private MemoryFilterProcessor processor;
    private TestInputTopic<String, UsedMemory> inputTopic;
    private TestOutputTopic<String, UsedMemory> outputTopic;

    private String stateStoreLocation = "${spring.kafka.streams.state.dir}";


    @BeforeEach
    void setUp() {
        props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, UsedMemorySerde.class.getCanonicalName());
        props.put(STATE_DIR_CONFIG, stateStoreLocation);

        processor = new MemoryFilterProcessor();
    }


    @Test
    void buildPipeline() throws Exception {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        this.processor.buildPipeline(streamsBuilder);

        Topology topology = streamsBuilder.build();

        try (TopologyTestDriver testDriver = new TopologyTestDriver(topology, props)) {

            inputTopic = testDriver.createInputTopic(
                    MemoryApplication.USED_MEMORY_TOPIC,
                    Serdes.String().serializer(),
                    new UsedMemorySerde());

            outputTopic =
                    testDriver.createOutputTopic(
                            MemoryFilterProcessor.FILTERED_USED_MEMORY,
                            Serdes.String().deserializer(),
                            new UsedMemorySerde());

            UsedMemory firstUsedMemory = new UsedMemory("10.10.10.1", 150L);
            UsedMemory secondUsedMemory = new UsedMemory("10.10.10.2", 200L);
            UsedMemory thirdUsedMemory = new UsedMemory("10.10.10.1", 250L);

            this.processor.setMessageFilterString("usedMemoryInKB < 200L");
            inputTopic.pipeInput(firstUsedMemory.getKey(), firstUsedMemory);
            inputTopic.pipeInput(secondUsedMemory.getKey(), secondUsedMemory);
            inputTopic.pipeInput(thirdUsedMemory.getKey(), thirdUsedMemory);

            List<KeyValue<String, UsedMemory>> actual = outputTopic.readKeyValuesToList();
            assertThat(actual.size(), is(1));

            this.processor.setMessageFilterString("usedMemoryInKB >= 200L");
            inputTopic.pipeInput(firstUsedMemory.getKey(), firstUsedMemory);
            inputTopic.pipeInput(secondUsedMemory.getKey(), secondUsedMemory);
            inputTopic.pipeInput(thirdUsedMemory.getKey(), thirdUsedMemory);

            actual = outputTopic.readKeyValuesToList();
            assertThat(actual.size(), is(2));
        }
    }
}