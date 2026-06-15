package com.airlinebooking.common.constans;

public final class RedisKeyConstants {
    private RedisKeyConstants() {}
    public static final String REGISTER_PENDING =
            "auth:register:pending:";

    public static final String REGISTER_OTP =
            "auth:register:otp";

    public static final String FOGOT_PASSWORD_OTP =
            "auth:forgot-password:otp";

    public static final String SIGN_IN_LOCK =
            "auth:login:lock:user:";

    public static final String LOCK_COUNT =
            "auth:login:lock-count:user:";

    public static final String LOCK_FOREVER =
            "auth:login:lock-forever:user:";
}
