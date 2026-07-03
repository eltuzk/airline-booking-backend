package com.airlinebooking.auth.controller;

import com.airlinebooking.auth.dto.request.*;
import com.airlinebooking.auth.dto.response.AuthResponse;
import com.airlinebooking.auth.dto.response.ChangePassResponse;
import com.airlinebooking.auth.services.AuthenticationServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationServices authenticationServices;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authenticationServices.signUp(registerRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest,
                                    HttpServletRequest request) {
        AuthResponse authResponse = authenticationServices.login(loginRequest, request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/change-first-password")
    public ResponseEntity<?> changeFirstPassword(@RequestBody ChangeFirstPassRequest changePassRequest,
                                                 HttpServletRequest request) {
        ChangePassResponse response = authenticationServices.firstChangePass(changePassRequest, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassRequest changePassRequest,
                                            HttpServletRequest request) {
        ChangePassResponse response = authenticationServices.changePassword(changePassRequest, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ChangePassResponse response = authenticationServices.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authenticationServices.verifyForgotPasswordOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                           HttpServletRequest httpRequest) {
        ChangePassResponse response = authenticationServices.resetPassword(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        AuthResponse response = authenticationServices.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authenticationServices.logout(request);
        return ResponseEntity.ok().build();
    }
}

