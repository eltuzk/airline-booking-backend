package com.airlinebooking.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST,"400","Dữ liệu không hợp lệ"),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "USER_001", "Email không đúng định dạng"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,"USER_002", "Email already exists"),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST,"USER_003", "Sai mật khẩu"),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "USER_004", "Mật khẩu phải có ít nhất 8 ký tự"),
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "USER_005", "Email không được để trống"),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "USER_006", "Mật khẩu không được để trống"),
    ACCOUNT_WAITING_ACTIVATION(HttpStatus.CONFLICT, "USER_007", "Tài khoản đang chờ xác thực"),
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "USER_008", "Tài khoản không tồn tại"),
    ACCOUNT_END_ACTIVATION(HttpStatus.BAD_REQUEST, "USER_009","Tài khoản đã hết hạn đăng ký"),
    PASSWORD_CONFIRMATION_MISMATCH(HttpStatus.BAD_REQUEST, "USER_010", "Mật khẩu xác nhận không khớp"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "ROLE_001", "Không tìm thấy role hợp lệ"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN_001", "Token không hợp lệ"),
    ACCOUNT_ALREADY_ACTIVE(HttpStatus.CONFLICT, "USER_011", "This account is already active on another device"),
    ACCOUNT_TEMPORARILY_LOCKED(HttpStatus.FORBIDDEN, "AUTH_001", "Tài khoản tạm thời bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau 15 phút"),
    ACCOUNT_PERMANENTLY_LOCKED(HttpStatus.FORBIDDEN, "AUTH_002", "Tài khoản đã bị khóa vĩnh viễn"),
    INVALID_LOGIN_ATTEMPT_LIMIT(HttpStatus.TOO_MANY_REQUESTS, "AUTH_003", "Vượt quá số lần đăng nhập sai cho phép"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_012", "Không tìm thấy tài khoản"),
    OTP_REQUIRED(HttpStatus.BAD_REQUEST, "OTP_001", "Mã OTP không được để trống"),
    OTP_INVALID(HttpStatus.BAD_REQUEST, "OTP_002", "Mã OTP không đúng hoặc đã hết hạn"),
    OLD_PASSWORD_WRONG(HttpStatus.BAD_REQUEST, "USER_013", "Mật khẩu hiện tại không đúng"),
    TOKEN_TYPE_INVALID(HttpStatus.FORBIDDEN, "AUTH_004", "Loại Token không hợp lệ");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
