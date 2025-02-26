package org.example.test_tasks_facieai.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TradeRecord {
    // Геттери та сеттери
    @JacksonXmlProperty(localName = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date date;

    @JacksonXmlProperty(localName = "productId")
    private String productId;

    @JacksonXmlProperty(localName = "currency")
    private String currency;

    @JacksonXmlProperty(localName = "price")
    private double price;

}
