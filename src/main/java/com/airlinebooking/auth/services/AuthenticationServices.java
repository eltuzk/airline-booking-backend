package com.airlinebooking.auth.services;

import com.airlinebooking.auth.dto.request.*;
import com.airlinebooking.auth.dto.response.AuthResponse;
import com.airlinebooking.auth.dto.response.ChangePassResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationServices {
    AuthResponse signUp(RegisterRequest newUser);
    AuthResponse login(LoginRequest user, HttpServletRequest request);
    ChangePassResponse firstChangePass(ChangeFirstPassRequest changePassRequest, HttpServletRequest request);
    void logout(HttpServletRequest request);

    /** Cấp lại access token bằng refresh token còn hiệu lực. */
    AuthResponse refreshToken(HttpServletRequest request);
    ChangePassResponse changePassword(ChangePassRequest request, HttpServletRequest httpRequest);

    /** Bước 1: Gửi OTP quên mật khẩu về email. */
    ChangePassResponse forgotPassword(ForgotPasswordRequest request);

    /** Bước 2: Xác minh OTP, trả về Token đặt lại mật khẩu. */
    AuthResponse verifyForgotPasswordOtp(VerifyOtpRequest request);

    /** Bước 3: Đặt lại mật khẩu bằng Token nhận được sau khi xác minh OTP. */
    ChangePassResponse resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest);
}