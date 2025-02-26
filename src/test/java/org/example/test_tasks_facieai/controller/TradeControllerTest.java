package org.example.test_tasks_facieai.controller;

import org.example.test_tasks_facieai.repository.ProductRepository;
import org.example.test_tasks_facieai.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TradeService tradeService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TradeController tradeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(tradeController).build();
    }

    @Test
    void testEnrichTrades_Success() throws Exception {
        // Імітація файлу trade.csv
        MockMultipartFile file = new MockMultipartFile(
                "file", "trade.csv", "text/csv", "date,productId,currency,price\n20230101,1,USD,100.25".getBytes()
        );

        when(tradeService.processTrades(any())).thenReturn("Processed CSV content");

        mockMvc.perform(multipart("/api/v1/enrich")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Processed CSV content"));

        verify(tradeService, times(1)).processTrades(any());
    }

    @Test
    void testUploadProducts_Success() throws Exception {
        // Імітація файлу product.csv
        MockMultipartFile file = new MockMultipartFile(
                "file", "product.csv", "text/csv", "productId,productName\n1,Sample Product".getBytes()
        );

        doNothing().when(productRepository).loadProductsFromFile(any());

        mockMvc.perform(multipart("/api/v1/enrich/upload-products")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Product data uploaded successfully"));

        verify(productRepository, times(1)).loadProductsFromFile(any());
    }
}
