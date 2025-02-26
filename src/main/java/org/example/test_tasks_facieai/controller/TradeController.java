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

    @PostMapping(value = "/csv", consumes = "multipart/form-data", produces = "text/csv")
    public ResponseEntity<String> enrichTradesCsv(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(tradeService.processTradesCsv(file));
    }

    @PostMapping(value = "/json", consumes = "application/json", produces = "text/csv")
    public ResponseEntity<String> enrichTradesJson(@RequestBody MultipartFile file) throws IOException {
        return ResponseEntity.ok(tradeService.processTradesJson(file));
    }

    @PostMapping(value = "/xml", consumes = "application/xml", produces = "text/csv")
    public ResponseEntity<String> enrichTradesXml(@RequestBody MultipartFile file) throws IOException {
        return ResponseEntity.ok(tradeService.processTradesXml(file));
    }

    @PostMapping("/upload-products")
    public String uploadProducts(@RequestParam("file") MultipartFile file) throws IOException {
        productRepository.loadProductsFromFile(file); // Використовуємо інжектований `productRepository`
        return "Product data uploaded successfully";
    }
}
