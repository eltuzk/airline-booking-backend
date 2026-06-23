package com.airlinebooking.payment.service;

public interface PaymentService {
    // Trả về một chuỗi String chính là cái đường link URL của VNPay
    String createVnPayPaymentUrl(Integer bookingId, String ipAddress);

    // Hàm này nhận toàn bộ dữ liệu VNPay gửi về, kiểm tra và update Database
    boolean processVnPayReturn(jakarta.servlet.http.HttpServletRequest request);
}