package org.forwardlogic.kafka.streams.memory;

import io.micrometer.common.util.StringUtils;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping(path = "/used-memory")
public class UsedMemoryController {
    private static final Logger logger = LoggerFactory.getLogger(UsedMemoryController.class);
    public static final String USED_MEMORY = "127.0.0.1.used-memory";

    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private KafkaTemplate<String, UsedMemory> kafkaTemplate;
    private MemoryFilterProcessor memoryFilterProcessor;
    private ExpressionParser parser;

    @Autowired
    public UsedMemoryController(StreamsBuilderFactoryBean streamsBuilderFactoryBean, KafkaTemplate<String, UsedMemory> kafkaTemplate, MemoryFilterProcessor memoryFilterProcessor) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.kafkaTemplate = kafkaTemplate;
        this.memoryFilterProcessor = memoryFilterProcessor;
        this.parser = new SpelExpressionParser();
    }

    @GetMapping(value = "/average")
    @ResponseStatus(HttpStatus.OK)
    public String getAverage() {
        KafkaStreams kafkaStreams = this.streamsBuilderFactoryBean.getKafkaStreams();
        ReadOnlyWindowStore<String, UsedMemoryCountAndSum> store = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(UsedMemoryAggregator.AGGREGATION_STORE, QueryableStoreTypes.windowStore())
        );

        Instant timeTo = Instant.now();
        Instant timeFrom = Instant.ofEpochMilli(1682614800000L);
        WindowStoreIterator<UsedMemoryCountAndSum> iterator = store.fetch(USED_MEMORY, timeFrom, timeTo);
        Float latestAverage = null;
        long latestWindowTimestanp = 0l;
        while (iterator.hasNext()) {
            KeyValue<Long, UsedMemoryCountAndSum> next = iterator.next();
            latestWindowTimestanp = next.key;
            latestAverage = next.value.getAverage();
            logger.info("Average used system memory @ time " + LocalDateTime.ofInstant(Instant.ofEpochMilli(latestWindowTimestanp), ZoneId.systemDefault()) + " is " + next.value.getAverage());
        }

        // close the iterator to release resources
        iterator.close();
        return "Average used system memory @ time " + LocalDateTime.ofInstant(Instant.ofEpochMilli(latestWindowTimestanp), ZoneId.systemDefault()) + " is " + latestAverage;
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createUsedMemory(@RequestBody UsedMemory usedMemory) {
        logger.info("Sending used memory: ", usedMemory);
        this.kafkaTemplate.send(MemoryApplication.USED_MEMORY_TOPIC, USED_MEMORY, usedMemory);
    }

    @PostMapping(value = "/memory-filter", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void setUsedMemoryFiter(@RequestBody MemoryFilter memoryFilter) throws Exception {
        if (StringUtils.isBlank(memoryFilter.filter())) {
            throw new BadArgumentsException("filter string is blank");
        }

        try {
            parser.parseExpression(memoryFilter.filter());
        } catch (ParseException parseEx) {
            throw new BadArgumentsException("filter " + memoryFilter.filter() + " is invalid");
        }

        logger.info("Setting used memory filter: ", memoryFilter);
        this.memoryFilterProcessor.setMessageFilterString(memoryFilter.filter());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadArgumentsException extends RuntimeException {

        public BadArgumentsException(String message) {
            super(message);
        }
    }
}
