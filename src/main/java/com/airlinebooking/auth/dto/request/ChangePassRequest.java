package com.airlinebooking.auth.dto.request;

import lombok.Data;

@Data

public class ChangePassRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPass;
}
