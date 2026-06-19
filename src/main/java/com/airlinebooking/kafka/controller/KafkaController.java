package com.airlinebooking.kafka.controller;

import com.airlinebooking.common.exception.ApiException;
import com.airlinebooking.common.exception.ErrorCode;
import com.airlinebooking.kafka.producer.KafkaProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaProducer kafkaProducer;

    public KafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping
    public String sendMessage(@RequestBody String message) {
        if (message == null || message.trim().isEmpty() || "{}".equals(message.trim())) {
            throw new ApiException(ErrorCode.INVALID_MESSAGE);
        }

        try {
            kafkaProducer.sendMessage(message);
            return "Message sent successfully: " + message;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.PROCESSING_KAFKA_MESSAGE);
        }
    }
}
