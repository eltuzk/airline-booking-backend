package com.airlinebooking.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    private final String code;
    private LocalDateTime timestamp;
}
