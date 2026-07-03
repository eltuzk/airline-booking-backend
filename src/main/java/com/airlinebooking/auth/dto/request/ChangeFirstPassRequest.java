package com.airlinebooking.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeFirstPassRequest {
    @NotBlank(message="{user.password.notblank}")
    @Size(min = 8, message="{user.password.size}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-z0-9]).+$",
            message = "Mật khẩu phải có chữ hoa, chữ thường và ít nhất 1 ký tự đặc biệt"
    )
    private String newPass;
    @NotBlank
    private String confirmPass;
}
