package com.airlinebooking.notification.template;

public class CheckInReminderTemplate {
    public static String build(String flightCode, String departureDate) {
        return "Xin nhắc Quý khách thực hiện check-in trực tuyến cho chuyến bay "
                + flightCode + " vào ngày " + departureDate + ".";
    }
}
