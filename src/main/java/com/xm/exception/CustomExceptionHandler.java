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
                .code(ErrorCode.UNSUPPORTED_CRYPTO.getCode())
                .message(ErrorCode.UNSUPPORTED_CRYPTO.getMessage())
                .details(List.of(ex.getLocalizedMessage()))
                .build();

        log.warn("{} {}", error.getMessage(), error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<ErrorDto> handleProvidedCryptoMetricsNotFoundException(
            ProvidedCryptoMetricsNotFoundException ex) {
        ErrorDto error = ErrorDto.builder()
                .code(ErrorCode.PROVIDED_CRYPTO_METRICS_NOT_FOUND.getCode())
                .message(ErrorCode.PROVIDED_CRYPTO_METRICS_NOT_FOUND.getMessage())
                .details(List.of(ex.getLocalizedMessage()))
                .build();

        log.warn("{} {}", error.getMessage(), error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public final ResponseEntity<ErrorDto> handleCryptoMetricsNotFoundException(CryptoMetricsNotFoundException ex) {
        ErrorDto error = ErrorDto.builder()
                .code(ErrorCode.CRYPTO_METRICS_NOT_FOUND.getCode())
                .message(ErrorCode.CRYPTO_METRICS_NOT_FOUND.getMessage())
                .details(List.of(ex.getLocalizedMessage()))
                .build();

        log.warn("{} {}", error.getMessage(), error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public final ResponseEntity<ErrorDto> handleDateWithoutNormalizedRangeException(
            DateWithoutNormalizedRangeException ex) {
        ErrorDto error = ErrorDto.builder()
                .code(ErrorCode.DATE_WITHOUT_NORMALIZED_RANGE_ENTRIES.getCode())
                .message(ErrorCode.DATE_WITHOUT_NORMALIZED_RANGE_ENTRIES.getMessage())
                .details(List.of(ex.getLocalizedMessage()))
                .build();

        log.warn("{} {}", error.getMessage(), error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public final ResponseEntity<ErrorDto> handleInvalidDateFormatException(InvalidDateFormatException ex) {
        ErrorDto error = ErrorDto.builder()
                .code(ErrorCode.INVALID_DATE_FORMAT.getCode())
                .message(ErrorCode.INVALID_DATE_FORMAT.getMessage())
                .details(List.of(ex.getLocalizedMessage()))
                .build();

        log.warn("{} {}", error.getMessage(), error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
