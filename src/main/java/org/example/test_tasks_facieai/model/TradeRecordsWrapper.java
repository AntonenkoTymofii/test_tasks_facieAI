package org.example.test_tasks_facieai.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Trades")
public class TradeRecordsWrapper {

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TradeRecord> trades;

    // Геттер та сеттер
    public List<TradeRecord> getTrades() { return trades; }
    public void setTrades(List<TradeRecord> trades) { this.trades = trades; }
}
