package com.xm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNSUPPORTED_CRYPTO("UNSUPPORTED_CRYPTO", "The provided crypto is not supported"),
    PROVIDED_CRYPTO_METRICS_NOT_FOUND("PROVIDED_CRYPTO_METRICS_NOT_FOUND",
            "Metrics not found for the provided crypto"),
    CRYPTO_METRICS_NOT_FOUND("CRYPTO_METRICS_NOT_FOUND", "Crypto metrics are not available");

    private final String code;
    private final String message;
}
