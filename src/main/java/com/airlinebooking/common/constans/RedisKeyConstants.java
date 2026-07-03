package com.airlinebooking.common.constans;

public final class RedisKeyConstants {
    private RedisKeyConstants() {}
    public static final String REGISTER_PENDING =
            "auth:register:pending:";

    public static final String LOGIN_SESSION =
            "auth:login:session:";

    /** Whitelist refresh token: auth:refresh:token:{userId} → jti */
    public static final String REFRESH_TOKEN =
            "auth:refresh:token:";

    /** TTL (ngày) của refresh token, khớp với thời gian hết hạn JWT refresh. */
    public static final long REFRESH_TOKEN_DAYS = 7L;
    public static final String FOGOT_PASSWORD_OTP =
            "auth:forgot-password:otp:";

    /** TTL (phút) của OTP quên mật khẩu. */
    public static final long FORGOT_PASSWORD_OTP_MINUTES = 10L;

    public static final String SIGN_IN_LOCK =
            "auth:login:lock:user:";

    public static final String LOCK_COUNT =
            "auth:login:lock-count:user:";

    public static final String LOCK_FOREVER =
            "auth:login:lock-forever:user:";

    public static final long SESSION_TIMEOUT_MINUTES = 30L;
    /** Số lần đăng nhập sai tối đa trước khi tài khoản bị khóa tạm thời. */
    public static final int MAX_FAIL_ATTEMPTS = 3;

    /** Thời gian khóa tạm thời (phút) sau khi đạt MAX_FAIL_ATTEMPTS lần sai. */
    public static final long LOGIN_FAIL_LOCK_MINUTES = 15L;

    /**
     * TTL (giờ) của marker "đã từng bị khóa tạm thời".
     * Phải lớn hơn LOGIN_FAIL_LOCK_MINUTES để marker còn tồn tại sau khi hết thời gian khóa.
     */
    public static final long LOCK_MARKER_TTL_HOURS = 24L;

    /** TTL (phút) cuộn cho key đếm số lần đăng nhập sai của mỗi lần thử. */
    public static final long FAIL_COUNT_TTL_MINUTES = 30L;

    public static final String NOTIFICATION_PROCESSED_EVENT = "notification:processed:";
    public static final long NOTIFICATION_PROCESSED_EVENT_HOURS = 24L;

}

