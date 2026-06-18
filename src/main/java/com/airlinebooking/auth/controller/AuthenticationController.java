package com.airlinebooking.auth.controller;

import com.airlinebooking.auth.dto.request.RegisterRequest;
import com.airlinebooking.auth.dto.response.AuthResponse;
import com.airlinebooking.auth.services.AuthenticationServices;
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
    public ResponseEntity<?> signUp(@Valid @RequestBody RegisterRequest registerRequest){

        AuthResponse authResponse = authenticationServices.signUp(registerRequest);
        return ResponseEntity.ok(authResponse);
    }
}
