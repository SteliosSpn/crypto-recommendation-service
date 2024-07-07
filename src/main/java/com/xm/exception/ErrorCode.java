package com.xm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNSUPPORTED_CRYPTO_EXCEPTION("UNSUPPORTED_CRYPTO_EXCEPTION", "The provided crypto is not supported"),
    CRYPTO_METRICS_NOT_FOUND_EXCEPTION("CRYPTO_METRICS_NOT_FOUND_EXCEPTION",
            "Metrics not found for the provided crypto");

    private final String code;
    private final String message;
}
