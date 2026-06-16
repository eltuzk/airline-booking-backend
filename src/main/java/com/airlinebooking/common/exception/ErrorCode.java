package com.airlinebooking.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,"USER_002", "Email already exists"),
    ACCOUNT_WAITING_ACTIVATION(HttpStatus.CONFLICT, "USER_003", "Tài khoản đang chờ xác thực");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
