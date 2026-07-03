package com.airlinebooking.kafka.event;

import java.util.UUID;

public class NotificationEvent {
    private String eventId;
    private NotificationType type;
    private String email;
    private String fullName;
    private String password;
    private String otp;
    private String flightCode;
    private String departureDate;
    private String fromCity;
    private String toCity;
    private Boolean success;
    private String transactionId;
    private String updateMessage;
    private String message;

    public static NotificationEvent register(String email, String fullName, String password) {
        NotificationEvent event = base(NotificationType.REGISTER, email);
        event.fullName = fullName;
        event.password = password;
        return event;
    }

    public static NotificationEvent forgotPassword(String email, String fullName, String otp) {
        NotificationEvent event = base(NotificationType.FORGOT_PASSWORD, email);
        event.fullName = fullName;
        event.otp = otp;
        return event;
    }

    public static NotificationEvent bookingConfirmation(
            String email,
            String flightCode,
            String departureDate,
            String fromCity,
            String toCity
    ) {
        NotificationEvent event = base(NotificationType.BOOKING_CONFIRMATION, email);
        event.flightCode = flightCode;
        event.departureDate = departureDate;
        event.fromCity = fromCity;
        event.toCity = toCity;
        return event;
    }

    public static NotificationEvent payment(String email, boolean success, String transactionId) {
        NotificationEvent event = base(NotificationType.PAYMENT, email);
        event.success = success;
        event.transactionId = transactionId;
        return event;
    }

    public static NotificationEvent flightUpdate(String email, String flightCode, String updateMessage) {
        NotificationEvent event = base(NotificationType.FLIGHT_UPDATE, email);
        event.flightCode = flightCode;
        event.updateMessage = updateMessage;
        return event;
    }

    public static NotificationEvent checkInReminder(String email, String flightCode, String departureDate) {
        NotificationEvent event = base(NotificationType.CHECKIN_REMINDER, email);
        event.flightCode = flightCode;
        event.departureDate = departureDate;
        return event;
    }

    public static NotificationEvent baggage(String email, String message) {
        NotificationEvent event = base(NotificationType.BAGGAGE, email);
        event.message = message;
        return event;
    }

    private static NotificationEvent base(NotificationType type, String email) {
        NotificationEvent event = new NotificationEvent();
        event.eventId = UUID.randomUUID().toString();
        event.type = type;
        event.email = email;
        return event;
    }

    public void validateBaseFields() {
        require(eventId, "eventId");
        require(email, "email");
        if (type == null) {
            throw new IllegalArgumentException("Kafka notification message is missing field: type");
        }
    }

    public String requireFullName() {
        return require(fullName, "fullName");
    }

    public String requirePassword() {
        return require(password, "password");
    }

    public String requireOtp() {
        return require(otp, "otp");
    }

    public String requireFlightCode() {
        return require(flightCode, "flightCode");
    }

    public String requireDepartureDate() {
        return require(departureDate, "departureDate");
    }

    public String requireFromCity() {
        return require(fromCity, "fromCity");
    }

    public String requireToCity() {
        return require(toCity, "toCity");
    }

    public boolean requireSuccess() {
        if (success == null) {
            throw new IllegalArgumentException("Kafka notification message is missing field: success");
        }
        return success;
    }

    public String requireTransactionId() {
        return require(transactionId, "transactionId");
    }

    public String requireUpdateMessage() {
        return require(updateMessage, "updateMessage");
    }

    public String requireMessage() {
        return require(message, "message");
    }

    private String require(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Kafka notification message is missing field: " + fieldName);
        }
        return value;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
