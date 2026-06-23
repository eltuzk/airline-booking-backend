package com.airlinebooking.payment.controller;

import com.airlinebooking.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(HttpServletRequest request){
        boolean isSuccess = paymentService.processVnPayReturn(request);

        if (isSuccess) {
            return ResponseEntity.ok("THANH TOÁN THÀNH CÔNG! Đơn hàng đã được cập nhật thành CONFIRMED.");
        } else {
            return ResponseEntity.badRequest().body("THANH TOÁN THẤT BẠI HOẶC CHỮ KÝ KHÔNG HỢP LỆ!");
        }
    }
}
