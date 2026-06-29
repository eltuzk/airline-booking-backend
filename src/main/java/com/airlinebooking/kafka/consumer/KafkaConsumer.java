package com.airlinebooking.kafka.consumer;

import com.airlinebooking.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public KafkaConsumer(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.notification}")
    public void getData(String data) {
        try {
            JsonNode json = objectMapper.readTree(data);
            String type = json.get("type").asText();
            String email = json.get("email").asText();

            switch (type) {
                case "BOOKING_CONFIRMATION":
                    notificationService.sendBookingConfirmation(
                            email,
                            json.get("flightCode").asText(),
                            json.get("departureDate").asText(),
                            json.get("fromCity").asText(),
                            json.get("toCity").asText()
                    );
                    break;
                case "PAYMENT":
                    notificationService.sendPaymentNotification(
                            email,
                            json.get("success").asBoolean(),
                            json.get("transactionId").asText()
                    );
                    break;
                case "FLIGHT_UPDATE":
                    notificationService.sendFlightUpdate(
                            email,
                            json.get("flightCode").asText(),
                            json.get("updateMessage").asText()
                    );
                    break;
                case "CHECKIN_REMINDER":
                    notificationService.sendCheckInReminder(
                            email,
                            json.get("flightCode").asText(),
                            json.get("departureDate").asText()
                    );
                    break;
                case "BAGGAGE":
                    notificationService.sendBaggageNotification(
                            email,
                            json.get("message").asText()
                    );
                    break;
                case "REGISTER":
                    notificationService.sendPassTemp(
                            email,
                            json.get("fullName").asText(),
                            json.get("password").asText()
                    );
                    break;
                case "FORGOT_PASSWORD":
                    notificationService.sendForgotPasswordOtp(
                            email,
                            json.get("fullName").asText(),
                            json.get("otp").asText()
                    );
                    break;

                default:
                    System.out.println("Unknown notification type: " + type);
            }
        } catch (Exception e) {
            logger.error("Error parsing message", e);
        }
    }
}
