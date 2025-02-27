package org.example.test_tasks_facieai.service;

import org.example.test_tasks_facieai.model.TradeRecord;
import org.example.test_tasks_facieai.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TradeServiceTest {

    private TradeService tradeService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        tradeService = new TradeService(productRepository);
    }

    @Test
    void testProcessTrades() {
        // Створюємо CSV-файл
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "tradeId,productId,currency,price\n1,1001,USD,50.0\n2,1002,EUR,30.5".getBytes(StandardCharsets.UTF_8)
        );

        // Мокаємо метод отримання назви продукту
        when(productRepository.getProductName("1001")).thenReturn("Product A");
        when(productRepository.getProductName("1002")).thenReturn("Product B");

        CompletableFuture<String> future = tradeService.processTrades(file);

        assertDoesNotThrow(() -> {
            String result = future.get();
            assertTrue(result.contains("Trade ID: 1 - Product: Product A"));
            assertTrue(result.contains("Trade ID: 2 - Product: Product B"));
        });

        verify(productRepository, times(2)).getProductName(anyString());
    }

    @Test
    void testUploadProducts() throws IOException {
        // Створюємо CSV-файл
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                "productId,productName\n1001,Product A\n1002,Product B".getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(productRepository).loadProductsFromFile(file);

        CompletableFuture<Void> future = tradeService.uploadProducts(file);

        assertDoesNotThrow(() -> future.get());
        verify(productRepository, times(1)).loadProductsFromFile(file);
    }

    @Test
    void testStreamTrades() {
        // Створюємо CSV-файл
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "stream.csv",
                "text/csv",
                "tradeId,productId,currency,price\n1,1001,USD,50.0\n2,1002,EUR,30.5".getBytes(StandardCharsets.UTF_8)
        );

        Flux<TradeRecord> tradeFlux = tradeService.streamTrades(file);

        StepVerifier.create(tradeFlux)
                .expectNextMatches(trade -> trade.getProductId().equals("1001") && trade.getCurrency().equals("USD"))
                .expectNextMatches(trade -> trade.getProductId().equals("1002") && trade.getCurrency().equals("EUR"))
                .verifyComplete();
    }
}
