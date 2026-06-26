package com.airlinebooking.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private boolean forceChangePass;
    private  String accessToken;
    private String refreshToken;
}
