package com.xm.service;

import com.xm.config.SupportedCryptocurrenciesConfig;
import com.xm.exception.CryptoMetricsNotFoundException;
import com.xm.exception.UnsupportedCryptoException;
import com.xm.model.dto.CryptoMetricsDto;
import com.xm.model.entity.CryptoMetricsEntity;
import com.xm.repository.CryptoMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CryptoRecommendationService {

    private final SupportedCryptocurrenciesConfig supportedCryptocurrenciesConfig;
    private final CryptoMetricsRepository cryptoMetricsRepository;
    private final ModelMapper modelMapper;
    public CryptoMetricsDto getCryptocurrencyMetrics(
            String cryptoId) {

        if (!supportedCryptocurrenciesConfig.getCryptocurrencies().contains(cryptoId)) {
            throw new UnsupportedCryptoException("Provided crypto "
                    + cryptoId + " is not supported in current version");
        }

        Optional<CryptoMetricsEntity> cryptoMetricsEntityOptional = cryptoMetricsRepository.findById(cryptoId);

        if (cryptoMetricsEntityOptional.isEmpty()) {
            throw new CryptoMetricsNotFoundException("Metrics not found for crypto " + cryptoId);
        }

        return modelMapper.map(cryptoMetricsEntityOptional.get(), CryptoMetricsDto.class);
    }
}
