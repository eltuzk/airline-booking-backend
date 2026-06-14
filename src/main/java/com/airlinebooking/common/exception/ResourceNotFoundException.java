package com.airlinebooking.common.exception;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
