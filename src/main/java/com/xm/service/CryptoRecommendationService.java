package com.xm.service;

import static com.xm.util.TimeUtil.getDate;
import static com.xm.util.TimeUtil.getEndOfDateTimestamp;

import com.xm.config.SupportedCryptocurrenciesConfig;
import com.xm.exception.CryptoMetricsNotFoundException;
import com.xm.exception.DateWithoutNormalizedRangeException;
import com.xm.exception.ProvidedCryptoMetricsNotFoundException;
import com.xm.exception.UnsupportedCryptoException;
import com.xm.model.dto.CryptoMetricsDto;
import com.xm.model.entity.CryptoMetricsEntity;
import com.xm.model.entity.CryptoPriceEntity;
import com.xm.repository.CryptoMetricsRepository;
import com.xm.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoRecommendationService {

    private final SupportedCryptocurrenciesConfig supportedCryptocurrenciesConfig;
    private final CryptoMetricsRepository cryptoMetricsRepository;
    private final ModelMapper modelMapper;
    private final CryptoPriceRepository cryptoPriceRepository;

    public CryptoMetricsDto getCryptocurrencyMetrics(
            String cryptoId) {

        if (!supportedCryptocurrenciesConfig.getCryptocurrencies().contains(cryptoId)) {
            throw new UnsupportedCryptoException("Provided crypto "
                    + cryptoId + " is not supported in current version");
        }

        Optional<CryptoMetricsEntity> cryptoMetricsEntityOptional = cryptoMetricsRepository.findById(cryptoId);

        if (cryptoMetricsEntityOptional.isEmpty()) {
            throw new ProvidedCryptoMetricsNotFoundException("Metrics not found for crypto " + cryptoId);
        }

        return modelMapper.map(cryptoMetricsEntityOptional.get(), CryptoMetricsDto.class);
    }

    public List<CryptoMetricsDto> getCryptoMetricsByDescendingNormalizedRange() {

        List<CryptoMetricsEntity> cryptoMetricsEntityList = cryptoMetricsRepository.findAll();
        if (cryptoMetricsEntityList.isEmpty()) {
            throw new CryptoMetricsNotFoundException("Crypto metrics not found in the database");
        }
        return cryptoMetricsEntityList.stream()
                .map(metricsEntity -> modelMapper.map(metricsEntity, CryptoMetricsDto.class))
                .map(this::getNormalizedRange)
                .sorted((c1, c2) -> c2.getNormalizedRange().compareTo(c1.getNormalizedRange()))
                .toList();
    }

    private CryptoMetricsDto getNormalizedRange(CryptoMetricsDto metrics) {
        BigDecimal normalizedRange = metrics.getMaxPrice()
                .subtract(metrics.getMinPrice())
                .divide(metrics.getMinPrice(), 2, RoundingMode.HALF_UP);
        metrics.setNormalizedRange(normalizedRange);
        return metrics;
    }

    public CryptoMetricsDto getHighestNormalizedRangeByDate(
            String requestedDate) {

            Date date = getDate(requestedDate);
            long startTimestamp = date.getTime();
            long endTimestamp = getEndOfDateTimestamp(date);

            List<CryptoPriceEntity> priceEntities = cryptoPriceRepository
                    .findByTimestampBetween(startTimestamp, endTimestamp);

            return priceEntities.stream()
                    .collect(Collectors.groupingBy(CryptoPriceEntity::getCryptocurrency))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .map(entry -> calculateMetrics(entry.getKey(), entry.getValue()))
                    .filter(cryptoDto -> cryptoDto.getMinPrice() != null)
                    .filter(cryptoDto -> cryptoDto.getMinPrice().compareTo(BigDecimal.ZERO) >0)
                    .max(Comparator.comparing(CryptoMetricsDto::getNormalizedRange))
                    .orElseThrow(() -> new DateWithoutNormalizedRangeException("No cryptocurrencies found with "
                            + "normalized range for the provided date"));


    }

    private CryptoMetricsDto calculateMetrics(
            String cryptocurrency,
            List<CryptoPriceEntity> prices) {
        BigDecimal minPrice = prices.stream()
                .map(CryptoPriceEntity::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(null);

        BigDecimal maxPrice = prices.stream()
                .map(CryptoPriceEntity::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(null);

        CryptoMetricsDto cryptoMetrics = CryptoMetricsDto.builder()
                .cryptocurrency(cryptocurrency)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal normalizedRange = maxPrice.subtract(minPrice)
                    .divide(minPrice, 2, RoundingMode.HALF_UP);
            cryptoMetrics.setNormalizedRange(normalizedRange);
        }

        return cryptoMetrics;
    }
}
