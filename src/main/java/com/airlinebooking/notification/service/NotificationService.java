package com.airlinebooking.notification.service;

import com.airlinebooking.notification.channel.EmailSender;
import com.airlinebooking.notification.template.*;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final EmailSender emailSender;

    public NotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendBookingConfirmation(String customerEmail, String flightCode, String departureDate, String fromCity, String toCity) {
        String body = BookingConfirmationTemplate.build(flightCode, departureDate, fromCity, toCity);
        emailSender.send(customerEmail, "Xác nhận đặt vé - " + flightCode, body);
    }

    public void sendPaymentNotification(String customerEmail, boolean success, String transactionId) {
        String subject = success ? "Thanh toán thành công" : "Thanh toán thất bại";
        String body = PaymentSuccessTemplate.build(success, transactionId);
        emailSender.send(customerEmail, subject, body);
    }

    public void sendFlightUpdate(String email, String flightCode, String updateMessage) {
        String body = FlightUpdateTemplate.build(flightCode, updateMessage);
        emailSender.send(email, "Cập nhật chuyến bay " + flightCode, body);
    }

    public void sendCheckInReminder(String email, String flightCode, String departureDate) {
        String body = CheckInReminderTemplate.build(flightCode, departureDate);
        emailSender.send(email, "Nhắc nhở check-in", body);
    }

    public void sendBaggageNotification(String email, String message) {
        String body = BaggageNotificationTemplate.build(message);
        emailSender.send(email, "Thông báo hành lý", body);
    }
    public void sendPassTemp(String email,String fullName ,String passTemp){
        String body = AccountCreatedTemplate.build(fullName,passTemp);
        emailSender.send(email, "Đăng ký tài khoản thành công", body);
    }

    public void sendForgotPasswordOtp(String email, String fullName, String otp) {
        String body = "Xin chào " + fullName + ",\n\n"
                + "Mã OTP đặt lại mật khẩu của bạn là: " + otp + "\n"
                + "Mã có hiệu lực trong 10 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.\n\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.";
        emailSender.send(email, "Đặt lại mật khẩu", body);
    }
}
