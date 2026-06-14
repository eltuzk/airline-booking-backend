package com.airlinebooking.notification.template;

public class FlightUpdateTemplate {
    public static String build(String flightCode, String updateMessage) {
        return "Thông báo chuyến bay " + flightCode + ":\n" + updateMessage;
    }
}
