package com.airlinebooking.payment.producer;

import com.airlinebooking.payment.event.PaymentSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaProducer{

    // Tên Topic cũng có thể đặt cụ thể ra để không nhầm với Topic của người khác
    private static final String PAYMENT_TOPIC = "booking-payment-completed-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;


    public void sendPaymentSuccessEvent(Integer bookingId) {
        try {
            PaymentSuccessEvent paymentSuccessEvent = new PaymentSuccessEvent(bookingId, "SUCCESS");

            // Dùng để biến Map thành chuỗi JSON chuẩn
            String jsonMessage = objectMapper.writeValueAsString(paymentSuccessEvent);

            kafkaTemplate.send(PAYMENT_TOPIC, jsonMessage);

            log.info("KAFKA PRODUCER: Đã phát thông báo: {}", jsonMessage);

        } catch (JsonProcessingException e) {
            log.error("Lỗi khi chuyển đổi JSON để gửi Kafka", e);
        }
    }
}
