package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping(path = "/used-memory")
public class UsedMemoryController {
    private static final Logger logger = LoggerFactory.getLogger(UsedMemoryController.class);
    public static final String USED_MEMORY = "127.0.0.1.used-memory";

    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    private KafkaTemplate<String, UsedMemory> kafkaTemplate;

    @Autowired
    public UsedMemoryController(StreamsBuilderFactoryBean streamsBuilderFactoryBean, KafkaTemplate<String, UsedMemory> kafkaTemplate) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/average")
    @ResponseStatus(HttpStatus.OK)
    public Float getAverage() {
        KafkaStreams kafkaStreams = this.streamsBuilderFactoryBean.getKafkaStreams();
        ReadOnlyWindowStore<String, UsedMemoryCountAndSum> store = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType("aggr", QueryableStoreTypes.windowStore())
        );

        Instant timeTo = Instant.now();
        Instant timeFrom = Instant.ofEpochMilli(1682614800000L);
        WindowStoreIterator<UsedMemoryCountAndSum> iterator = store.fetch(USED_MEMORY, timeFrom, timeTo);
        Float latest = null;
        while (iterator.hasNext()) {
            KeyValue<Long, UsedMemoryCountAndSum> next = iterator.next();
            long windowTimestamp = next.key;
            latest = next.value.getAverage();
            logger.info("Average used system memory @ time " + LocalDateTime.ofInstant(Instant.ofEpochMilli(windowTimestamp), ZoneId.systemDefault()) + " is " + next.value.getAverage());
        }

        // close the iterator to release resources
        iterator.close();
        return latest;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createUsedMemory(@RequestBody UsedMemory usedMemory) {
        logger.info("Sending used memory: ", usedMemory);
        this.kafkaTemplate.send(MemoryApplication.ANALYTICS_USED_MEMORY_TOPIC, USED_MEMORY, usedMemory);
    }

}
