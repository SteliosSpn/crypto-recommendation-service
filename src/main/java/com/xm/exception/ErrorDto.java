package com.xm.exception;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ErrorDto {

    private String code;
    private String message;
    private List<String> details;
}
