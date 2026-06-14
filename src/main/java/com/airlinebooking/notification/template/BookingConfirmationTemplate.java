package com.airlinebooking.notification.template;

public class BookingConfirmationTemplate {
    public static String build(String flightCode, String departureDate, String fromCity, String toCity) {
        return "Xin chào Quý khách,\n\n"
                + "Chúng tôi xác nhận đặt vé thành công.\n"
                + "- Chuyến bay: " + flightCode + "\n"
                + "- Ngày khởi hành: " + departureDate + "\n"
                + "- Điểm đi: " + fromCity + "\n"
                + "- Điểm đến: " + toCity + "\n\n"
                + "Vui lòng check-in trực tuyến trước giờ bay.\n\n"
                + "Trân trọng,\nAirline Booking";
    }
}
