package org.example.test_tasks_facieai.service;

import org.example.test_tasks_facieai.model.TradeRecord;
import org.example.test_tasks_facieai.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
public class TradeService {

    private final ProductRepository productRepository;

    public TradeService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public CompletableFuture<String> processTrades(MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().skip(1).forEach(line -> {
                    String[] parts = line.split(",");
                    if (parts.length < 4) return;
                    TradeRecord trade = new TradeRecord();
                    trade.setProductId(parts[1]);
                    trade.setCurrency(parts[2]);
                    trade.setPrice(Double.parseDouble(parts[3]));
                    String productName = getProductName(trade.getProductId());
                    result.append("Trade ID: ").append(parts[0]).append(" - Product: ").append(productName).append("\n");
                });
            } catch (Exception e) {
                throw new RuntimeException("Error processing trades", e);
            }
            return result.toString();
        });
    }

    public CompletableFuture<Void> uploadProducts(MultipartFile file) {
        return CompletableFuture.runAsync(() -> {
            try {
                productRepository.loadProductsFromFile(file);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading products", e);
            }
        });
    }

    public Flux<TradeRecord> streamTrades(MultipartFile file) {
        return Flux.create(sink -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().skip(1).forEach(line -> {
                    String[] parts = line.split(",");
                    if (parts.length < 4) return;
                    TradeRecord trade = new TradeRecord();
                    trade.setProductId(parts[1]);
                    trade.setCurrency(parts[2]);
                    trade.setPrice(Double.parseDouble(parts[3]));
                    sink.next(trade);
                });
                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    public String getProductName(String productId) {
        return productRepository.getProductName(productId);
    }
}
