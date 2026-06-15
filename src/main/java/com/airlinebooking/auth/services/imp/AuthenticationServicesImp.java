package com.airlinebooking.auth.services.imp;

import com.airlinebooking.auth.dto.request.RegisterRequest;
import com.airlinebooking.auth.dto.response.AuthResponse;
import com.airlinebooking.auth.entity.UserEntity;
import com.airlinebooking.auth.repository.UserRepository;
import com.airlinebooking.auth.services.AuthenticationServices;
import com.airlinebooking.common.exception.ErrorCode;
import com.airlinebooking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServicesImp implements AuthenticationServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String , Object> redisTemplate;




    @Override
    public AuthResponse signUp(RegisterRequest newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            throw new ResourceNotFoundException(ErrorCode.EMAIL_ALREADY_EXISTS);
        } else{
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(newUser.getEmail());


        }
        return null;
    }
}
