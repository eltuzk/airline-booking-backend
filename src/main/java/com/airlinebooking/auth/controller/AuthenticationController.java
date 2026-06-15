package com.airlinebooking.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(){
        return ResponseEntity.ok().build();
    }
}
