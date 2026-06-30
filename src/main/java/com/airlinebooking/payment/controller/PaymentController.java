package com.airlinebooking.payment.controller;

import com.airlinebooking.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;

    // API tạo link thanh toán
    @GetMapping("/create-url")
    public ResponseEntity<?> createPaymentUrl(@RequestParam Integer bookingId, HttpServletRequest request) {
        // Lấy IP ảo của khách hàng
        String ipAddress = request.getRemoteAddr();

        String paymentUrl = paymentService.createVnPayPaymentUrl(bookingId, ipAddress);

        return ResponseEntity.ok(paymentUrl);
    }

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
