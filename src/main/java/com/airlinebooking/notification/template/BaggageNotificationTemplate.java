package com.airlinebooking.notification.template;

public class BaggageNotificationTemplate {
    public static String build(String message) {
        return "Thông báo hành lý:\n" + message;
    }
}
