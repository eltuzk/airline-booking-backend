package com.airlinebooking.auth.services;

import com.airlinebooking.auth.dto.request.ChangeFirstPassRequest;
import com.airlinebooking.auth.dto.request.ChangePassRequest;
import com.airlinebooking.auth.dto.request.LoginRequest;
import com.airlinebooking.auth.dto.request.RegisterRequest;
import com.airlinebooking.auth.dto.response.AuthResponse;
import com.airlinebooking.auth.dto.response.ChangePassResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationServices {
    AuthResponse signUp(RegisterRequest newUser);
    AuthResponse login(LoginRequest user, HttpServletRequest request);
    ChangePassResponse firstChangePass(ChangeFirstPassRequest changePassRequest, HttpServletRequest request);
    void logout(HttpServletRequest request);
}