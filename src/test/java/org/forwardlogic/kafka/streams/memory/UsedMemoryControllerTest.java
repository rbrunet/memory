package org.forwardlogic.kafka.streams.memory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsedMemoryController.class)
class UsedMemoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @MockBean
    private KafkaTemplate<String, UsedMemory> kafkaTemplate;

    @MockBean
    private MemoryFilterProcessor memoryFilterProcessor;

    @Test
    void test_set_used_memory_fiter_OK() throws Exception {
        this.mockMvc.perform(post("/used-memory/memory-filter").content("{\"filter\":\"usedMemoryInKB > 400\"}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void test_set_used_memory_fiter_error() throws Exception {
        this.mockMvc.perform(post("/used-memory/memory-filter").contentType(MediaType.APPLICATION_JSON).content("{\"filter\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().equals("filter string is blank")));
    }
}