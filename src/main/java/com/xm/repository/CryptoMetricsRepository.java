package com.xm.repository;

import com.xm.model.entity.CryptoMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoMetricsRepository extends JpaRepository<CryptoMetricsEntity, String> {
}
