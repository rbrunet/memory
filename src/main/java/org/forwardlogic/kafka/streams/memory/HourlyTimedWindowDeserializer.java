package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.kstream.TimeWindowedDeserializer;

import java.time.Duration;

public class HourlyTimedWindowDeserializer extends TimeWindowedDeserializer<String> {

    public HourlyTimedWindowDeserializer() {
        super(new StringDeserializer(), Duration.ofHours(1).toMillis());
    }
}
