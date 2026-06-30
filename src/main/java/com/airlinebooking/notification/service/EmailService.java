package com.airlinebooking.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendPaymentSuccessEmail(String toEmail, Integer bookingId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dia_chi_email_cua_ban@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Xác nhận thanh toán vé máy bay thành công - Mã Đơn: " + bookingId);
            message.setText("Chào bạn,\n\nĐơn đặt vé máy bay của bạn (ID: " + bookingId + ") đã được thanh toán thành công.\nCảm ơn bạn đã sử dụng dịch vụ của Airline Booking!");

            javaMailSender.send(message);
            log.info("Đã gửi email xác nhận thành công tới: {}", toEmail);

        } catch (Exception e) {
            log.error("Lỗi khi gửi email tới {}: {}", toEmail, e.getMessage());
        }
    }
}