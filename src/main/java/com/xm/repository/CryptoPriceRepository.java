package com.xm.repository;

import com.xm.model.entity.CryptoPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CryptoPriceRepository extends JpaRepository<CryptoPriceEntity, Long> {

    List<CryptoPriceEntity> findByCryptocurrency(String cryptocurrency);

    List<CryptoPriceEntity> findByTimestampBetween(Long startTimestamp, Long endTimestamp);
}
