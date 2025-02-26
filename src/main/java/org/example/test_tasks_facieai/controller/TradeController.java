package org.example.test_tasks_facieai.controller;

import org.example.test_tasks_facieai.repository.ProductRepository;
import org.example.test_tasks_facieai.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/enrich")
public class TradeController {

    private final TradeService tradeService;
    private final ProductRepository productRepository;

    public TradeController(TradeService tradeService, ProductRepository productRepository) {
        this.tradeService = tradeService;
        this.productRepository = productRepository;
    }

    @PostMapping(consumes = "multipart/form-data", produces = "text/csv")
    public ResponseEntity<String> enrichTrades(@RequestParam("file") MultipartFile file) throws IOException {
        String result = tradeService.processTrades(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/upload-products")
    public String uploadProducts(@RequestParam("file") MultipartFile file) throws IOException {
        productRepository.loadProductsFromFile(file); // Використовуємо інжектований `productRepository`
        return "Product data uploaded successfully";
    }
}
