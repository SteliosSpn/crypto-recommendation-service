package com.xm.repository;

import com.xm.model.entity.CryptoMetricsEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CryptoMetricsRepository extends JpaRepository<CryptoMetricsEntity, String> {

    @Cacheable("cryptoMetricsCache")
    Optional<CryptoMetricsEntity> findById(String id);
}
