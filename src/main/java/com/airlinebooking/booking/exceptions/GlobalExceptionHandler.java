package com.airlinebooking.booking.exceptions;

import com.airlinebooking.booking.payload.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // bắt tất cả lỗi kế thừa từ AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse> handAppException(AppException e){
        BaseResponse baseResponse = new BaseResponse();

        ErrorCode errorCode = e.getErrorCode();
        baseResponse.setCode(errorCode.getCode());
        baseResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(baseResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleUnwantedException(Exception e){
        BaseResponse baseResponse = new BaseResponse();



        baseResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        baseResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() + e.getMessage());

        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(baseResponse);

    }

}
