package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class UsedMemoryAggregator {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        KStream<String, UsedMemory> stream = streamsBuilder.stream(MemoryApplication.ANALYTICS_USED_MEMORY_TOPIC, Consumed.with(new Serdes.StringSerde(),
                new UsedMemorySerde()));

        KTable<Windowed<String>, UsedMemoryCountAndSum> countAndSumStream = stream.groupByKey().windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofHours(1))).aggregate(() -> new UsedMemoryCountAndSum(0L, 0.0F), (key, value, aggregate) -> {
            if (Objects.nonNull(value)) {
                aggregate.setCount(aggregate.getCount() + 1);
                aggregate.setSum(aggregate.getSum() + value.usedMemoryInKB());
                aggregate.computeAverage();
            }
            return aggregate;
        }, Materialized.<String, UsedMemoryCountAndSum, WindowStore<Bytes, byte[]>>as("aggr"));

        countAndSumStream.toStream().map((windowedKey, value) -> KeyValue.pair(windowedKey.key(), value)).to("aggregated-used-memory", Produced.with(Serdes.String(), new UsedMemoryCountAndSumSerde()));
    }
}
