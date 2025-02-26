package org.example.test_tasks_facieai.service;

import org.example.test_tasks_facieai.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import static org.mockito.Mockito.lenient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TradeService tradeService;


    @BeforeEach
    void setup() {
        lenient().when(productRepository.getProductName("1")).thenReturn("Treasury Bills Domestic");
        lenient().when(productRepository.getProductName("2")).thenReturn("Corporate Bonds Domestic");
        lenient().when(productRepository.getProductName("3")).thenReturn("REPO Domestic");
        lenient().when(productRepository.getProductName("99")).thenReturn("Missing Product Name");
    }


    @Test
    void testProcessTrades_Success() throws IOException {
        // Імітація файлу trade.csv
        String csvContent = "date,productId,currency,price\n" +
                "20230101,1,USD,100.25\n" +
                "20230102,2,EUR,200.45\n" +
                "20230103,3,GBP,300.50\n";

        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", csvContent.getBytes());

        String result = tradeService.processTrades(file);

        String expected = "date,productName,currency,price\n" +
                "20230101,Treasury Bills Domestic,USD,100.25\n" +
                "20230102,Corporate Bonds Domestic,EUR,200.45\n" +
                "20230103,REPO Domestic,GBP,300.50\n";

        assertEquals(expected, result);

        verify(productRepository, times(1)).getProductName("1");
        verify(productRepository, times(1)).getProductName("2");
        verify(productRepository, times(1)).getProductName("3");
    }

    @Test
    void testProcessTrades_InvalidDate_Ignored() throws IOException {
        // CSV із некоректною датою
        String csvContent = "date,productId,currency,price\n" +
                "invalidDate,1,USD,100.25\n" +
                "20230101,2,EUR,200.45\n";

        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", csvContent.getBytes());

        String result = tradeService.processTrades(file);

        String expected = "date,productName,currency,price\n" +
                "20230101,Corporate Bonds Domestic,EUR,200.45\n"; // Перший рядок пропущено

        assertEquals(expected, result);

        verify(productRepository, times(1)).getProductName("2");
        verify(productRepository, never()).getProductName("1");
    }

    @Test
    void testProcessTrades_MissingProductName() throws IOException {
        // Продукт із відсутнім ID
        String csvContent = "date,productId,currency,price\n" +
                "20230101,99,USD,100.25\n";

        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", csvContent.getBytes());

        String result = tradeService.processTrades(file);

        String expected = "date,productName,currency,price\n" +
                "20230101,Missing Product Name,USD,100.25\n";

        assertEquals(expected, result);

        verify(productRepository, times(1)).getProductName("99");
    }

    @Test
    void testIsValidDate_ValidFormat() {
        assertTrue(tradeService.isValidDate("20230101"));
        assertTrue(tradeService.isValidDate("19991231"));
    }

    @Test
    void testIsValidDate_InvalidFormat() {
        assertFalse(tradeService.isValidDate("2023-01-01"));
        assertFalse(tradeService.isValidDate("20231301")); // Некоректний місяць
        assertFalse(tradeService.isValidDate("abcd1234")); // Некоректний формат
    }
}
