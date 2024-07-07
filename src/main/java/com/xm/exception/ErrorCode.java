package com.xm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNSUPPORTED_CRYPTO("UNSUPPORTED_CRYPTO", "The provided crypto is not supported"),
    PROVIDED_CRYPTO_METRICS_NOT_FOUND("PROVIDED_CRYPTO_METRICS_NOT_FOUND",
            "Metrics not found for the provided crypto"),
    CRYPTO_METRICS_NOT_FOUND("CRYPTO_METRICS_NOT_FOUND", "Crypto metrics are not available"),
    DATE_WITHOUT_NORMALIZED_RANGE_ENTRIES("DATE_WITHOUT_NORMALIZED_RANGE_ENTRIES",
            "Provided date has no normalized range entry"),
    INVALID_DATE_FORMAT("INVALID_DATE_FORMAT", "The provided date is not a valid date format");

    private final String code;
    private final String message;
}
