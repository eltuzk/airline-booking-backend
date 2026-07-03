package com.airlinebooking.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "PASSWORD_INVALID")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).+$",
            message = "Mật khẩu phải có chữ hoa, chữ thường và ít nhất 1 ký tự đặc biệt"
    )
    private String newPassword;

    @NotBlank(message = "PASSWORD_REQUIRED")
    private String confirmPass;
}