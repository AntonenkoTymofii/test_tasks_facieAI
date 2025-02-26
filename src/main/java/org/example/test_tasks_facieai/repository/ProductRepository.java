package org.example.test_tasks_facieai.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ProductRepository {
    private final Map<String, String> productCache = new HashMap<>();
    private final Jedis redisClient;

    @Autowired
    public ProductRepository(Jedis redisClient) { // Автовпровадження біну Jedis
        this.redisClient = redisClient;
    }

    public void loadProductsFromFile(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                if (parts.length < 2) return;
                productCache.put(parts[0], parts[1]);
                redisClient.set(parts[0], parts[1]);
            });
        }
    }

    public String getProductName(String productId) {
        String productName = redisClient.get(productId);
        return productName != null ? productName : "Missing Product Name";
    }
}

