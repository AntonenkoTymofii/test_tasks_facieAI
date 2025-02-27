package org.example.test_tasks_facieai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(TradeController.class)
public class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testEnrichTrades() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", // Назва параметра у запиті
                "test.csv", // Ім'я файлу
                MediaType.TEXT_PLAIN_VALUE, // Тип вмісту
                "tradeId,productId,currency,price\n1,1001,USD,50.0".getBytes() // Вміст файлу
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich/process")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Processed Trades")); // Очікуваний результат
    }
}
