package org.example.test_tasks_facieai.service;

import org.example.test_tasks_facieai.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class TradeService {
    private final ProductRepository productRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public TradeService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String processTrades(MultipartFile file) throws IOException {
        StringBuilder result = new StringBuilder("date,productName,currency,price\n");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines().skip(1).forEach(line -> {
                String[] fields = line.split(",");
                if (fields.length < 4) return;

                String date = fields[0];
                String productId = fields[1];
                String currency = fields[2];
                String price = fields[3];

                if (!isValidDate(date)) {
                    System.err.println("Invalid date: " + date);
                    return;
                }

                String productName = productRepository.getProductName(productId);
                result.append(String.join(",", date, productName, currency, price)).append("\n");
            });
        }
        return result.toString();
    }

    boolean isValidDate(String date) {
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
