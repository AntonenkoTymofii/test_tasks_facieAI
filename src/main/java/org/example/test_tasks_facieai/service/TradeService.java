package org.example.test_tasks_facieai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.test_tasks_facieai.model.TradeRecord;
import org.example.test_tasks_facieai.model.TradeRecordsWrapper;
import org.example.test_tasks_facieai.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradeService {
    private final ProductRepository productRepository;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public TradeService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.jsonMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
    }

    public String processTradesCsv(MultipartFile file) throws IOException {
        List<TradeRecord> trades = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines().skip(1).forEach(line -> {
                String[] fields = line.split(",");
                if (fields.length < 4) return;

                try {
                    TradeRecord trade = new TradeRecord();
                    trade.setDate(dateFormat.parse(fields[0]));
                    trade.setProductId(fields[1]);
                    trade.setCurrency(fields[2]);
                    trade.setPrice(Double.parseDouble(fields[3]));
                    trades.add(trade);
                } catch (ParseException | NumberFormatException e) {
                    System.err.println("Invalid record: " + line);
                }
            });
        }
        return processTrades(trades);
    }

    public String processTradesJson(MultipartFile file) throws IOException {
        List<TradeRecord> trades = jsonMapper.readValue(file.getInputStream(),
                jsonMapper.getTypeFactory().constructCollectionType(List.class, TradeRecord.class));
        return processTrades(trades);
    }

    public String processTradesXml(MultipartFile file) throws IOException {
        TradeRecordsWrapper wrapper = xmlMapper.readValue(file.getInputStream(), TradeRecordsWrapper.class);
        return processTrades(wrapper.getTrades());
    }

    public String processTrades(List<TradeRecord> trades) {
        StringBuilder result = new StringBuilder("date,productName,currency,price\n");
        for (TradeRecord trade : trades) {
            String productName = productRepository.getProductName(trade.getProductId());
            result.append(dateFormat.format(trade.getDate())).append(",")
                    .append(productName).append(",")
                    .append(trade.getCurrency()).append(",")
                    .append(trade.getPrice()).append("\n");
        }
        return result.toString();
    }
}
