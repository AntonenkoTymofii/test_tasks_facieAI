package org.example.test_tasks_facieai.controller;

import org.example.test_tasks_facieai.model.TradeRecord;
import org.example.test_tasks_facieai.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/enrich")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping(value = "/process", consumes = {"multipart/form-data", "application/json", "application/xml"}, produces = "text/csv")
    public CompletableFuture<ResponseEntity<String>> enrichTrades(@RequestParam("file") MultipartFile file) {
        return tradeService.processTrades(file)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping(value = "/upload-products", consumes = {"multipart/form-data", "application/json", "application/xml"})
    public CompletableFuture<ResponseEntity<String>> uploadProducts(@RequestParam("file") MultipartFile file) {
        return tradeService.uploadProducts(file)
                .thenApply(result -> ResponseEntity.ok("Product data uploaded successfully"));
    }

    @PostMapping(value = "/stream-trades", consumes = {"multipart/form-data", "application/json", "application/xml"}, produces = "text/event-stream")
    public Flux<TradeRecord> streamTrades(@RequestParam("file") MultipartFile file) {
        return tradeService.streamTrades(file);
    }
}