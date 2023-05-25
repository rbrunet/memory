package org.forwardlogic.kafka.streams.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class UsedMemoryCountAndSumSerde implements Serde<UsedMemoryCountAndSum>, Serializer<UsedMemoryCountAndSum>, Deserializer<UsedMemoryCountAndSum> {
    private ObjectMapper objectMapper = new ObjectMapper();

    public byte[] serialize(String topic, UsedMemoryCountAndSum data) {
        byte[] retVal = null;
        try {
            retVal = this.objectMapper.writeValueAsString(data).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return retVal;
    }

    @Override
    public UsedMemoryCountAndSum deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, UsedMemoryCountAndSum.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

    @Override
    public Serializer<UsedMemoryCountAndSum> serializer() {
        return this;
    }

    @Override
    public Deserializer<UsedMemoryCountAndSum> deserializer() {
        return this;
    }
}
