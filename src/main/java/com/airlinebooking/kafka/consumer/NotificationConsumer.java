package com.airlinebooking.kafka.consumer;

import com.airlinebooking.cache.services.RedisService;
import com.airlinebooking.common.constans.RedisKeyConstants;
import com.airlinebooking.kafka.event.NotificationEvent;
import com.airlinebooking.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
public class NotificationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final RedisService redisService;

    public NotificationConsumer(
            ObjectMapper objectMapper,
            NotificationService notificationService,
            RedisService redisService
    ) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.redisService = redisService;
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.notification}")
    public void getData(String data) {
        NotificationEvent event = parseEvent(data);
        event.validateBaseFields();

        if (isProcessed(event)) {
            logger.info("Skip duplicated notification eventId={}", event.getEventId());
            return;
        }

        handle(event);
        markProcessed(event);
    }

    private NotificationEvent parseEvent(String data) {
        try {
            return objectMapper.readValue(data, NotificationEvent.class);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Invalid Kafka notification JSON", e);
        }
    }

    private void handle(NotificationEvent event) {
        switch (event.getType()) {
            case BOOKING_CONFIRMATION -> sendBookingConfirmation(event);
            case PAYMENT -> sendPaymentNotification(event);
            case FLIGHT_UPDATE -> sendFlightUpdate(event);
            case CHECKIN_REMINDER -> sendCheckInReminder(event);
            case BAGGAGE -> sendBaggageNotification(event);
            case REGISTER -> sendRegisterNotification(event);
            case FORGOT_PASSWORD -> sendForgotPasswordOtp(event);
        }
    }

    private void sendBookingConfirmation(NotificationEvent event) {
        notificationService.sendBookingConfirmation(
                event.getEmail(),
                event.requireFlightCode(),
                event.requireDepartureDate(),
                event.requireFromCity(),
                event.requireToCity()
        );
    }

    private void sendPaymentNotification(NotificationEvent event) {
        notificationService.sendPaymentNotification(
                event.getEmail(),
                event.requireSuccess(),
                event.requireTransactionId()
        );
    }

    private void sendFlightUpdate(NotificationEvent event) {
        notificationService.sendFlightUpdate(
                event.getEmail(),
                event.requireFlightCode(),
                event.requireUpdateMessage()
        );
    }

    private void sendCheckInReminder(NotificationEvent event) {
        notificationService.sendCheckInReminder(
                event.getEmail(),
                event.requireFlightCode(),
                event.requireDepartureDate()
        );
    }

    private void sendBaggageNotification(NotificationEvent event) {
        notificationService.sendBaggageNotification(
                event.getEmail(),
                event.requireMessage()
        );
    }

    private void sendRegisterNotification(NotificationEvent event) {
        notificationService.sendPassTemp(
                event.getEmail(),
                event.requireFullName(),
                event.requirePassword()
        );
    }

    private void sendForgotPasswordOtp(NotificationEvent event) {
        notificationService.sendForgotPasswordOtp(
                event.getEmail(),
                event.requireFullName(),
                event.requireOtp()
        );
    }

    private boolean isProcessed(NotificationEvent event) {
        return redisService.get(processedKey(event)) != null;
    }

    private void markProcessed(NotificationEvent event) {
        redisService.set(
                processedKey(event),
                true,
                RedisKeyConstants.NOTIFICATION_PROCESSED_EVENT_HOURS,
                TimeUnit.HOURS
        );
    }

    private String processedKey(NotificationEvent event) {
        return RedisKeyConstants.NOTIFICATION_PROCESSED_EVENT + event.getEventId();
    }
}
