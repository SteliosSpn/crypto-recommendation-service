package com.xm.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptoMetricsDto {

    private String cryptocurrency;
    private Long oldestTimestamp;
    private Long newestTimestamp;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal normalizedRange;
}
