package com.airlinebooking.auth.services.imp;

import com.airlinebooking.auth.dto.request.RegisterRequest;
import com.airlinebooking.auth.dto.response.AuthResponse;
import com.airlinebooking.auth.entity.UserEntity;
import com.airlinebooking.auth.repository.UserRepository;
import com.airlinebooking.auth.services.AuthenticationServices;
import com.airlinebooking.common.constans.RedisKeyConstants;
import com.airlinebooking.common.dto.RegisterCache;
import com.airlinebooking.common.exception.ErrorCode;
import com.airlinebooking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.time.Duration;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServicesImp implements AuthenticationServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String , Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;




    @Override
    public AuthResponse signUp(RegisterRequest newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            throw new ResourceNotFoundException(ErrorCode.EMAIL_ALREADY_EXISTS);
        };
        String redisKey = RedisKeyConstants.REGISTER_PENDING + newUser.getEmail();
        if(Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_WAITING_ACTIVATION);
        }

        String tempPassword = generateTempPassword();

        RegisterCache registerCache =
                RegisterCache.builder()
                        .password(tempPassword)
                        .email(newUser.getEmail())
                        .build();
        redisTemplate.opsForValue().set(
                redisKey,
                registerCache,
                Duration.ofMinutes(15)
        );

        ObjectNode payload =
                objectMapper.createObjectNode();
        payload.put(
                "type",
                "REGISTER"
        );

        payload.put(
                "email",
                newUser.getEmail()
        );

        payload.put(
                "fullName",
                newUser.getFirstName() + " " + newUser.getLastName()
        );

        payload.put(
                "password",
                tempPassword
        );

        kafkaTemplate.send(
                "notification-topic",
                payload.toString()
        );



        return AuthResponse.builder()
                .message(
                        "Vui lòng kiểm tra email để xác nhận đăng ký"
                )
                .build();
    }

    private String generateTempPassword(){
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }
}
