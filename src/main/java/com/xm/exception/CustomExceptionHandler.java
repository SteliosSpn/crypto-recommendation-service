package com.xm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<ErrorDto> handleUnsupportedCryptoException(UnsupportedCryptoException ex) {
        ErrorDto error = ErrorDto.builder()
                .code(ErrorCode.UNSUPPORTED_CRYPTO_EXCEPTION.getCode())
                .message(ErrorCode.UNSUPPORTED_CRYPTO_EXCEPTION.getMessage())
                .details(List.of(ex.getLocalizedMessage()))
                .build();

        log.warn(error.getMessage() + " " + error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<ErrorDto> handleCryptoMetricsNotFoundException(CryptoMetricsNotFoundException ex) {
        ErrorDto error = ErrorDto.builder()
                .code(ErrorCode.CRYPTO_METRICS_NOT_FOUND_EXCEPTION.getCode())
                .message(ErrorCode.CRYPTO_METRICS_NOT_FOUND_EXCEPTION.getMessage())
                .details(List.of(ex.getLocalizedMessage()))
                .build();

        log.warn(error.getMessage() + " " + error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
