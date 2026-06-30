package com.airlinebooking.payment.service;

public interface BookingKafkaProducerService {

    void sendPaymentSuccessEvent(Integer bookingId);
}
