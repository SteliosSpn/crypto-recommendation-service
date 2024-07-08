package com.xm.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;

@Entity
@Table(name = "crypto_metrics")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoMetricsEntity {
    @Id
    private String cryptocurrency;
    private Long oldestTimestamp;
    private Long newestTimestamp;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
