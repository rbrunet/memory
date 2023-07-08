package org.forwardlogic.kafka.streams.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MessageProvider {

    private KafkaTemplate<String, UsedMemory> kafkaTemplate;
    private Random rd;

    @Autowired
    public void MessageProvider(KafkaTemplate<String, UsedMemory> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.rd = new Random(4566);
    }

    @Scheduled(fixedRate = 5_000L)
    public void sendMessages() {
        this.kafkaTemplate.send(MemoryApplication.USED_MEMORY_TOPIC, "192.168.1.1.used-memory" ,new UsedMemory("192.168.1.1", 250 + 10*(this.rd.nextInt(50))));
    }
}
