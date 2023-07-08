package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.forwardlogic.kafka.streams.memory.persistence.UsedMemoryAggregationEntity;
import org.forwardlogic.kafka.streams.memory.persistence.UsedMemoryAggregationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsedMemoryCollectorTest {

    private UsedMemoryCollector usedMemoryCollector;

    @Mock
    private UsedMemoryAggregationRepository repository;

    @BeforeEach
    void setUp() {
        this.usedMemoryCollector = new UsedMemoryCollector(repository);
    }

    @Test
    void test_receive_one_average() throws Exception {
        UsedMemoryCountAndSum usedMemoryAggregation = new UsedMemoryCountAndSum(123L, 45.6F, "192.168.1.1", 123L);
        ArgumentCaptor<UsedMemoryAggregationEntity> argumentCaptor = ArgumentCaptor.forClass(UsedMemoryAggregationEntity.class);
        Windowed<String> key = new Windowed<>(usedMemoryAggregation.getHostAddress() + ".used-memory", new TimeWindow(5L, 199L));
        ConsumerRecord<Windowed<String>, UsedMemoryCountAndSum> record =
                new ConsumerRecord<>(UsedMemoryAggregator.AGGREGATED_USED_MEMORY_TOPIC, 0, 0, key, usedMemoryAggregation);
        this.usedMemoryCollector.collect(record);

        verify(this.repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getTimestamp(), is(5L));
    }
}
