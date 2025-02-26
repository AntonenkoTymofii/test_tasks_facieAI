package org.example.test_tasks_facieai.repository;

import org.example.test_tasks_facieai.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private Jedis jedisMock;

    @InjectMocks
    private ProductRepository productRepository;

    @Mock
    private MultipartFile multipartFileMock;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository(jedisMock);
    }

    @Test
    void testLoadProductsFromFile_Success() throws IOException {
        String fileContent = "id,name\n1,Treasury Bills\n2,Credit Default Swaps";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());

        when(multipartFileMock.getInputStream()).thenReturn(inputStream);

        productRepository.loadProductsFromFile(multipartFileMock);

        verify(jedisMock, times(1)).set("1", "Treasury Bills");
        verify(jedisMock, times(1)).set("2", "Credit Default Swaps");
    }

    @Test
    void testGetProductName_ExistingProduct() {
        when(jedisMock.get("1")).thenReturn("Treasury Bills");

        String result = productRepository.getProductName("1");

        assertEquals("Treasury Bills", result);
    }

    @Test
    void testGetProductName_MissingProduct() {
        when(jedisMock.get("3")).thenReturn(null);

        String result = productRepository.getProductName("3");

        assertEquals("Missing Product Name", result);
    }
}