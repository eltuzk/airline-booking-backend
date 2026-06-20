package com.airlinebooking.booking.exceptions;

import com.airlinebooking.booking.payload.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CentralException {

    // bắt tất cả lỗi kế thừa từ AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse> handAppException(AppException e){
        BaseResponse baseResponse = new BaseResponse();

        baseResponse.setCode(e.getCode());
        baseResponse.setMessage(e.getMessage());

        return ResponseEntity.status(e.getCode()).body(baseResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleUnwantedException(Exception e){
        BaseResponse baseResponse = new BaseResponse();

        baseResponse.setCode(500);
        baseResponse.setMessage("Lỗi hệ thống không xác định được: " + e.getMessage());

        return ResponseEntity.status(500).body(baseResponse);

    }

}
