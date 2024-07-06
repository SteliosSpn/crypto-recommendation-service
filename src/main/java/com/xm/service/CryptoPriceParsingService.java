package com.xm.service;

import static com.xm.util.TimeUtil.isValidUnixTimestamp;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.xm.config.SupportedCryptocurrenciesConfig;
import com.xm.model.entity.CryptoMetricsEntity;
import com.xm.model.entity.CryptoPriceEntity;
import com.xm.repository.CryptoMetricsRepository;
import com.xm.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoPriceParsingService {

    private final SupportedCryptocurrenciesConfig supportedCryptocurrenciesConfig;
    private final CryptoPriceRepository cryptoPriceRepository;
    private final CryptoMetricsRepository cryptoMetricsRepository;

    public void processCsvContent(String csvContent) {
        try (CSVReader reader = new CSVReader(new StringReader(csvContent))) {
            List<String[]> cryptoEntries = reader.readAll();
            List<CryptoPriceEntity> entities = cryptoEntries.parallelStream()
                    .filter(this::isValidCryptocurrencyEntry)
                    .map(this::getCryptoPriceEntity)
                    .collect(Collectors.toList());

            cryptoPriceRepository.saveAll(entities);

            calculateAndUpdateMetrics(entities);
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidCryptocurrencyEntry(String[] cryptoEntry) {
        if (cryptoEntry.length != 3) {
            return false;
        }
        if (!isValidUnixTimestamp(cryptoEntry[0])) {
            return false;
        }
        if (!supportedCryptocurrenciesConfig.getCryptocurrencies().contains(cryptoEntry[1])) {
            return false;
        }
        if (!NumberUtils.isParsable(cryptoEntry[2])
                || new BigDecimal(cryptoEntry[2]).compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        return true;
    }

    private CryptoPriceEntity getCryptoPriceEntity(String[] cryptoEntry) {
        return CryptoPriceEntity.builder()
                .timestamp(Long.parseLong(cryptoEntry[0]))
                .cryptocurrency(cryptoEntry[1])
                .price(new BigDecimal(cryptoEntry[2]))
                .build();
    }

    private void calculateAndUpdateMetrics(List<CryptoPriceEntity> newEntities) {
        Map<String, List<CryptoPriceEntity>> groupedByCrypto = newEntities.stream()
                .collect(Collectors.groupingBy(CryptoPriceEntity::getCryptocurrency));

        for (Map.Entry<String, List<CryptoPriceEntity>> entry : groupedByCrypto.entrySet()) {
            String crypto = entry.getKey();
            List<CryptoPriceEntity> prices = entry.getValue();

            CryptoMetricsEntity metricsEntity = getOrCreateMetrics(crypto, prices);

            prices.forEach(price -> overrideMetrics(price, metricsEntity));

            cryptoMetricsRepository.save(metricsEntity);

            log.info("Metrics for {} Updated: Oldest = {}, Newest = {}, Min Price = {}, Max Price = {}",
                    crypto,
                    metricsEntity.getOldestTimestamp(),
                    metricsEntity.getNewestTimestamp(),
                    metricsEntity.getMinPrice(),
                    metricsEntity.getMaxPrice());
        }
    }

    private CryptoMetricsEntity getOrCreateMetrics(
            String crypto,
            List<CryptoPriceEntity> prices) {
        Optional<CryptoMetricsEntity> optionalMetrics = cryptoMetricsRepository.findById(crypto);

        if (optionalMetrics.isPresent()) {
            return optionalMetrics.get();
        }

        CryptoPriceEntity firstPriceEntity = prices.get(0);
        prices.remove(0);
        return CryptoMetricsEntity.builder()
                .cryptocurrency(crypto)
                .oldestTimestamp(firstPriceEntity.getTimestamp())
                .newestTimestamp(firstPriceEntity.getTimestamp())
                .minPrice(firstPriceEntity.getPrice())
                .maxPrice(firstPriceEntity.getPrice())
                .build();

    }

    private void overrideMetrics(
            CryptoPriceEntity priceEntity,
            CryptoMetricsEntity metrics) {

        if (priceEntity.getTimestamp() < metrics.getOldestTimestamp()) {
            metrics.setOldestTimestamp(priceEntity.getTimestamp());
        }

        if (priceEntity.getTimestamp() > metrics.getNewestTimestamp()) {
            metrics.setNewestTimestamp(priceEntity.getTimestamp());
        }

        if (priceEntity.getPrice().compareTo(metrics.getMinPrice()) < 0) {
            metrics.setMinPrice(priceEntity.getPrice());
        }

        if (priceEntity.getPrice().compareTo(metrics.getMaxPrice()) > 0) {
            metrics.setMaxPrice(priceEntity.getPrice());
        }
    }

}
