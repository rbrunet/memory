package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MemoryFilterProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MemoryFilterProcessor.class);
    public static final String FILTERED_USED_MEMORY = "filtered-used-memory";
    private final AtomicReference<String> messageFilterString = new AtomicReference<>();
    private final ExpressionParser expressionParser;

    public MemoryFilterProcessor() {
        this.expressionParser = new SpelExpressionParser();
    }

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        KStream<String, UsedMemory> stream = streamsBuilder.stream(MemoryApplication.USED_MEMORY_TOPIC, Consumed.with(new Serdes.StringSerde(),
                new UsedMemorySerde()));

        stream.filter((key, message) -> filterMessage(key, message))
                .to(FILTERED_USED_MEMORY);
    }

    public void setMessageFilterString(String filter) {
        this.messageFilterString.set(filter);
    }

    private boolean filterMessage(String key, UsedMemory message) {
        String filterString = this.messageFilterString.get();
        if (Objects.isNull(filterString)) {
            return true;
        }

        Boolean value = true;
        try {
            value = this.expressionParser
                    .parseExpression(filterString)
                    .getValue(new StandardEvaluationContext(message), Boolean.class);
        } catch (ParseException parseException) {
            logger.error("Cannot parse filter {}", filterString, parseException);
            value = true;
        } catch (EvaluationException evaluationException) {
            logger.error("Cannot evaluate filter {} with message {}", filterString, message, evaluationException);
            value = true;
        }

        return value;
    }
}
