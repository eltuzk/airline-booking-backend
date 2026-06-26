package com.airlinebooking.auth.controller;

import com.airlinebooking.auth.dto.request.ChangeFirstPassRequest;
import com.airlinebooking.auth.dto.request.ChangePassRequest;
import com.airlinebooking.auth.dto.request.LoginRequest;
import com.airlinebooking.auth.dto.request.RegisterRequest;
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
    public ResponseEntity<?> signUp(@Valid @RequestBody RegisterRequest registerRequest){

        AuthResponse authResponse = authenticationServices.signUp(registerRequest);
        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest,
                                    HttpServletRequest request){
        AuthResponse authResponse = authenticationServices.login(loginRequest, request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/change-first-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangeFirstPassRequest changePassRequest,
                                            HttpServletRequest request){
        ChangePassResponse response = authenticationServices.firstChangePass(changePassRequest, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        authenticationServices.logout(request);
        return ResponseEntity.ok().build();
    }


}
