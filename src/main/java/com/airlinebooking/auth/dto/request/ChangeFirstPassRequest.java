package com.airlinebooking.auth.dto.request;

import lombok.Data;

@Data
public class ChangeFirstPassRequest {
    private String newPass;
    private String confirmPass;
}
