package com.xm.service;

import static com.xm.util.TimeUtil.isValidUnixTimestamp;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.xm.config.SupportedCryptocurrenciesConfig;
import com.xm.model.entity.CryptoPriceEntity;
import com.xm.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoPriceParsingService {

    private final SupportedCryptocurrenciesConfig supportedCryptocurrenciesConfig;
    private final CryptoPriceRepository cryptoPriceRepository;

    public void processCsvContent(String csvContent) {
        try (CSVReader reader = new CSVReader(new StringReader(csvContent))) {
            List<String[]> cryptoEntries = reader.readAll();
            cryptoEntries.parallelStream()
                    .filter(this::isValidCryptocurrencyEntry)
                    .map(this::getCryptoPriceEntity)
                    .forEach(cryptoPriceRepository::save);
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
}
