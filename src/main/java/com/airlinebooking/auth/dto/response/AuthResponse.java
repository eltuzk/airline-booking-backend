package com.airlinebooking.auth.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private UserDTO user;
    private String accessToken;
}
