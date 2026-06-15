package com.airlinebooking.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String notificationTopic;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, @Value("${spring.kafka.topic.notification}") String notificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationTopic = notificationTopic;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(notificationTopic, message);
        logger.info("Sent notification: {}", message);
    }
}
