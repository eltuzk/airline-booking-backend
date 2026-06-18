package com.airlinebooking.auth.services;

import com.airlinebooking.auth.dto.request.RegisterRequest;
import com.airlinebooking.auth.dto.response.AuthResponse;

public interface AuthenticationServices {
    AuthResponse signUp(RegisterRequest newUser);
}
