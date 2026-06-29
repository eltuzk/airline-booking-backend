package com.airlinebooking.auth.services.imp;

import com.airlinebooking.auth.dto.request.ChangeFirstPassRequest;
import com.airlinebooking.auth.dto.request.ChangePassRequest;
import com.airlinebooking.auth.dto.request.ForgotPasswordRequest;
import com.airlinebooking.auth.dto.request.LoginRequest;
import com.airlinebooking.auth.dto.request.RegisterRequest;
import com.airlinebooking.auth.dto.request.ResetPasswordRequest;
import com.airlinebooking.auth.dto.request.VerifyOtpRequest;
import com.airlinebooking.auth.dto.response.AuthResponse;
import com.airlinebooking.auth.dto.response.ChangePassResponse;
import com.airlinebooking.auth.entity.RoleEntity;
import com.airlinebooking.auth.entity.UserEntity;
import com.airlinebooking.auth.mapper.UserMapper;
import com.airlinebooking.auth.repository.RoleRepository;
import com.airlinebooking.auth.repository.UserRepository;
import com.airlinebooking.auth.services.AuthenticationServices;
import com.airlinebooking.cache.services.RedisService;
import com.airlinebooking.common.constans.RedisKeyConstants;
import com.airlinebooking.common.dto.RegisterCache;
import com.airlinebooking.common.dto.UserDTO;
import com.airlinebooking.common.exception.ErrorCode;
import com.airlinebooking.common.exception.ResourceNotFoundException;
import com.airlinebooking.common.util.JwtUtil;
import com.airlinebooking.common.dto.LoginSessionCache;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServicesImp implements AuthenticationServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String , Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;


    @Override
    public AuthResponse signUp(RegisterRequest newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            throw new ResourceNotFoundException(ErrorCode.EMAIL_ALREADY_EXISTS);
        };
        String redisKey = RedisKeyConstants.REGISTER_PENDING + newUser.getEmail();
        if(Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_WAITING_ACTIVATION);
        }

        String tempPassword = generateTempPassword();
        String hashTempPassword = passwordEncoder.encode(tempPassword);
// Lưu redis
        RegisterCache registerCache =
                RegisterCache.builder()
                        .firstName(newUser.getFirstName())
                        .lastName(newUser.getLastName())
                        .fullName(newUser.getFirstName() + " " + newUser.getLastName())
                        .phone(newUser.getPhone())
                        .role("ROLE_USER")
                        .password(hashTempPassword)
                        .email(newUser.getEmail())
                        .build();
        redisTemplate.opsForValue().set(
                redisKey,
                registerCache,
                Duration.ofMinutes(15)
        );

        ObjectNode payload =
                objectMapper.createObjectNode();
        payload.put(
                "type",
                "REGISTER"
        );

        payload.put(
                "email",
                newUser.getEmail()
        );

        payload.put(
                "fullName",
                newUser.getFirstName() + " " + newUser.getLastName()
        );

        payload.put(
                "password",
                tempPassword
        );

        kafkaTemplate.send(
                "notification-topic",
                payload.toString()
        );



        return AuthResponse.builder()
                .message(
                        "Vui lòng kiểm tra email để xác nhận đăng ký"
                )
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest user, HttpServletRequest request) {
        String email = user.getEmail();

        // ── Bước 1: Kiểm tra khóa tạm thời (fast-path, không cần truy vấn DB) ──────────
        if (redisService.get(RedisKeyConstants.SIGN_IN_LOCK + email) != null) {
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_TEMPORARILY_LOCKED);
        }

        // ── Bước 2: Xử lý tài khoản chờ kích hoạt (đăng nhập lần đầu bằng mật khẩu tạm) ─────
        String pendingKey = RedisKeyConstants.REGISTER_PENDING + email;
        RegisterCache pendingUser = (RegisterCache) redisService.get(pendingKey);

        if (pendingUser != null) {
            if (!passwordEncoder.matches(user.getPassword(), pendingUser.getPassword())) {
                // Tài khoản chờ kích hoạt chưa có bản ghi trong DB, không áp dụng khóa vĩnh viễn
                handleFailedAttempt(email, null);
            }
            // Mật khẩu tạm đúng → xóa bộ đếm, cấp Token đổi mật khẩu
            clearFailAttempts(email);
            String changePasswordToken = jwtUtil.generateChangePasswordToken(pendingUser.getEmail());
            return AuthResponse.builder()
                    .forceChangePass(true)
                    .accessToken(changePasswordToken)
                    .build();
        }

        // ── Bước 3: Xử lý tài khoản đã kích hoạt ────────────────────────────────────────
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.LOGIN_FAIL)
        );

        // ── Bước 4: Kiểm tra khóa vĩnh viễn (lưu trong DB) ─────────────────────────────
        if (Boolean.TRUE.equals(userEntity.getPermanentlyLocked())) {
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_PERMANENTLY_LOCKED);
        }

        // ── Bước 5: Xác thực mật khẩu ────────────────────────────────────────────────
        if (!passwordEncoder.matches(user.getPassword(), userEntity.getPasswordHash())) {
            handleFailedAttempt(email, userEntity); // luôn throw exception
        }

        // ── Bước 6: Đăng nhập thành công — đặt lại bộ đếm và tạo Session ───────────────────
        clearFailAttempts(email);

        String sessionKey = RedisKeyConstants.LOGIN_SESSION + userEntity.getUserId();
        LoginSessionCache existingSession = (LoginSessionCache) redisService.get(sessionKey);
        String clientIp = getClientIp(request);

        if (existingSession != null && !existingSession.getIpAddress().equals(clientIp)) {
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
        }

        LoginSessionCache session = LoginSessionCache.builder()
                .email(userEntity.getEmail())
                .ipAddress(clientIp)
                .lastActivityTime(System.currentTimeMillis())
                .build();
        redisService.set(sessionKey, session, RedisKeyConstants.SESSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);

        UserDTO userDTO = UserMapper.mapToDTO(userEntity);
        String accessToken = jwtUtil.generateAccessToken(userDTO);
        String refreshToken = jwtUtil.generateRefreshToken(userDTO);

        // Lưu jti của refresh token vào Redis whitelist (7 ngày)
        Claims refreshClaims = jwtUtil.parseToken(refreshToken);
        redisService.set(
                RedisKeyConstants.REFRESH_TOKEN + userEntity.getUserId(),
                refreshClaims.getId(),
                RedisKeyConstants.REFRESH_TOKEN_DAYS,
                TimeUnit.DAYS
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = extractBearerToken(request);
        Claims claims = jwtUtil.parseAccessToken(token);
        String userId = claims.getSubject();
        redisService.delete(RedisKeyConstants.LOGIN_SESSION + userId);
        redisService.delete(RedisKeyConstants.REFRESH_TOKEN + userId);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    public ChangePassResponse firstChangePass(ChangeFirstPassRequest changePassRequest, HttpServletRequest request) {
        String token = extractBearerToken(request);
        Claims claim = jwtUtil.parseAccessToken(token);
        String type = claim.get("type").toString();
        if(!"CHANGE_PASS".equals(type)) {
            throw new ResourceNotFoundException(ErrorCode.INVALID_TOKEN);
        }
        String email = claim.getSubject();
        String redisKey = RedisKeyConstants.REGISTER_PENDING + email;
        RegisterCache pendingUser = (RegisterCache) redisService.get(redisKey);
        if(pendingUser == null) {
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_END_ACTIVATION);
        }
        if(!changePassRequest.getNewPass().equals(changePassRequest.getConfirmPass())) {
            throw new ResourceNotFoundException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }

        RoleEntity role = roleRepository.findByRoleName("ROLE_USER").orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.INVALID_ROLE)
        );

        UserEntity user = new  UserEntity();
        user.setEmail(pendingUser.getEmail());
        user.setFullName(pendingUser.getFullName());
        user.setFirstName(pendingUser.getFirstName());
        user.setLastName(pendingUser.getLastName());
        user.setPhoneNumber(pendingUser.getPhone());
        user.setPasswordHash(passwordEncoder.encode(changePassRequest.getNewPass()));
        user.setRole(role);
        user.setStatus("ACTIVE");
        userRepository.save(user);

        redisService.delete(redisKey);

        return ChangePassResponse.builder()
                .message("Hoàn tất đăng ký tài khoản")
                .build();
    }

    /**
     * Tăng bộ đếm số lần đăng nhập sai cho email tương ứng.
     * Tùy theo số lần đếm và lịch sử khóa trước đó, phương thức sẽ throw một trong các exception:
     *   - PASSWORD_WRONG               (count < MAX_FAIL_ATTEMPTS)
     *   - ACCOUNT_TEMPORARILY_LOCKED   (count == MAX_FAIL_ATTEMPTS, vòng đầu tiên)
     *   - ACCOUNT_PERMANENTLY_LOCKED   (count == MAX_FAIL_ATTEMPTS, vòng thứ hai)
     *
     * Phương thức này LUÔN throw exception — nơi gọi không cần throw thêm.
     *
     * Lưu ý: Bộ đếm sử dụng mô hình get/set đơn giản. Với môi trường có nhiều yêu cầu đồng thời,
     * nên thay thế bằng thao tác INCR nguyên tử của Redis.
     */
    private void handleFailedAttempt(String email, UserEntity userEntity) {
        String countKey = RedisKeyConstants.LOCK_COUNT + email;
        String lockKey  = RedisKeyConstants.SIGN_IN_LOCK + email;
        String markerKey = RedisKeyConstants.LOCK_FOREVER + email;

        Integer count = (Integer) redisService.get(countKey);
        if (count == null) count = 0;
        count++;

        if (count < RedisKeyConstants.MAX_FAIL_ATTEMPTS) {
            // Vẫn còn trong giới hạn cho phép — lưu bộ đếm với TTL cuộn
            redisService.set(countKey, count, RedisKeyConstants.FAIL_COUNT_TTL_MINUTES, TimeUnit.MINUTES);
            throw new ResourceNotFoundException(ErrorCode.PASSWORD_WRONG);
        }

        // count == MAX_FAIL_ATTEMPTS: quyết định giữa khóa tạm thời và khóa vĩnh viễn
        if (redisService.get(markerKey) != null) {
            // Vòng thứ hai sai mật khẩu sau lần khóa tạm trước → khóa vĩnh viễn tài khoản
            if (userEntity != null) {
                userEntity.setPermanentlyLocked(true);
                userEntity.setPermanentlyLockedAt(LocalDateTime.now());
                userRepository.save(userEntity);
            }
            // Dọn dẹp Redis — không cần giữ lại bộ đếm/marker cho tài khoản đã khóa vĩnh viễn
            redisService.delete(countKey);
            redisService.delete(markerKey);
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_PERMANENTLY_LOCKED);
        }

        // Vòng đầu tiên: áp dụng khóa tạm thời trong LOGIN_FAIL_LOCK_MINUTES phút
        redisService.set(lockKey, Boolean.TRUE, RedisKeyConstants.LOGIN_FAIL_LOCK_MINUTES, TimeUnit.MINUTES);
        // Đánh dấu "đã từng bị khóa tạm" để vòng sai mật khẩu tiếp theo kích hoạt khóa vĩnh viễn
        redisService.set(markerKey, Boolean.TRUE, RedisKeyConstants.LOCK_MARKER_TTL_HOURS, TimeUnit.HOURS);
        // Đặt lại bộ đếm để vòng thứ hai bắt đầu từ 0
        redisService.delete(countKey);
        throw new ResourceNotFoundException(ErrorCode.ACCOUNT_TEMPORARILY_LOCKED);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Đổi mật khẩu (tài khoản đang đăng nhập)
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public ChangePassResponse changePassword(ChangePassRequest changePassRequest, HttpServletRequest httpRequest) {
        // Lấy userId từ access token
        String token = extractBearerToken(httpRequest);
        Claims claims = jwtUtil.parseAccessToken(token);
        String userId = claims.getSubject();

        UserEntity user = userRepository.findById(Long.parseLong(userId)).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.LOGIN_FAIL)
        );

        // Xác minh mật khẩu cũ
        if (!passwordEncoder.matches(changePassRequest.getOldPassword(), user.getPasswordHash())) {
            throw new ResourceNotFoundException(ErrorCode.OLD_PASSWORD_WRONG);
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu khớp nhau
        if (!changePassRequest.getNewPassword().equals(changePassRequest.getConfirmPass())) {
            throw new ResourceNotFoundException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }

        user.setPasswordHash(passwordEncoder.encode(changePassRequest.getNewPassword()));
        userRepository.save(user);

        // Huỷ session và refresh token — người dùng cần đăng nhập lại
        redisService.delete(RedisKeyConstants.LOGIN_SESSION + userId);
        revokeRefreshToken(userId);

        return ChangePassResponse.builder()
                .message("Đổi mật khẩu thành công. Vui lòng đăng nhập lại.")
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Quên mật khẩu — Bước 1: Gửi OTP
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public ChangePassResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();

        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.LOGIN_FAIL)
        );

        if (Boolean.TRUE.equals(user.getPermanentlyLocked())) {
            throw new ResourceNotFoundException(ErrorCode.ACCOUNT_PERMANENTLY_LOCKED);
        }

        // Tạo OTP 6 chữ số ngẫu nhiên và lưu vào Redis với TTL 10 phút
        String otp = String.format("%06d", (int) (Math.random() * 1_000_000));
        redisService.set(
                RedisKeyConstants.FOGOT_PASSWORD_OTP + email,
                otp,
                RedisKeyConstants.FORGOT_PASSWORD_OTP_MINUTES,
                TimeUnit.MINUTES
        );

        // Gửi OTP qua Kafka → NotificationService → Email
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("type", "FORGOT_PASSWORD");
        payload.put("email", email);
        payload.put("fullName", user.getFullName());
        payload.put("otp", otp);
        kafkaTemplate.send("notification-topic", payload.toString());

        return ChangePassResponse.builder()
                .message("Mã OTP đã được gửi đến email của bạn. Mã có hiệu lực trong 10 phút.")
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Quên mật khẩu — Bước 2: Xác minh OTP
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse verifyForgotPasswordOtp(VerifyOtpRequest request) {
        String otpKey = RedisKeyConstants.FOGOT_PASSWORD_OTP + request.getEmail();
        String storedOtp = (String) redisService.get(otpKey);

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new ResourceNotFoundException(ErrorCode.OTP_INVALID);
        }

        // OTP hợp lệ — xóa khỏi Redis để chống dùng lại
        redisService.delete(otpKey);

        // Cấp Token đặt lại mật khẩu (dùng chung accessKey, type = RESET_PASS)
        String resetToken = jwtUtil.generateResetPasswordToken(request.getEmail());

        return AuthResponse.builder()
                .accessToken(resetToken)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Quên mật khẩu — Bước 3: Đặt lại mật khẩu
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public ChangePassResponse resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
        String token = extractBearerToken(httpRequest);
        Claims claims = jwtUtil.parseAccessToken(token);

        // Kiểm tra loại Token phải là RESET_PASS
        String type = claims.get("type", String.class);
        if (!"RESET_PASS".equals(type)) {
            throw new ResourceNotFoundException(ErrorCode.TOKEN_TYPE_INVALID);
        }

        if (!request.getNewPassword().equals(request.getConfirmPass())) {
            throw new ResourceNotFoundException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }

        String email = claims.getSubject();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.LOGIN_FAIL)
        );

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Huỷ session và refresh token nếu có — bắt buộc đăng nhập lại
        String userIdStr = String.valueOf(user.getUserId());
        redisService.delete(RedisKeyConstants.LOGIN_SESSION + userIdStr);
        revokeRefreshToken(userIdStr);

        return ChangePassResponse.builder()
                .message("Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.")
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Tiện ích dùng chung
    // ─────────────────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────────────────
    // Refresh Token
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse refreshToken(HttpServletRequest request) {
        String token = extractBearerToken(request);
        Claims claims = jwtUtil.parseToken(token); // dùng refreshKey để parse

        String userId = claims.getSubject();
        String jti = claims.getId();

        // Kiểm tra jti trong Redis whitelist
        String storedJti = (String) redisService.get(RedisKeyConstants.REFRESH_TOKEN + userId);
        if (storedJti == null || !storedJti.equals(jti)) {
            throw new ResourceNotFoundException(ErrorCode.INVALID_TOKEN);
        }

        // Tạo UserDTO từ claims của refresh token (không cần query DB)
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(Long.parseLong(userId));
        userDTO.setEmail(claims.get("email", String.class));
        userDTO.setRole(claims.get("role", String.class));

        String newAccessToken = jwtUtil.generateAccessToken(userDTO);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    /** Thu hồi refresh token của userId khỏi Redis whitelist. */
    private void revokeRefreshToken(String userId) {
        redisService.delete(RedisKeyConstants.REFRESH_TOKEN + userId);
    }

    /** Trích xuất Bearer Token từ header Authorization, throw nếu không hợp lệ. */
    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResourceNotFoundException(ErrorCode.INVALID_TOKEN);
        }
        return header.substring(7);
    }

    /**
     * Xóa tất cả các key Redis liên quan đến đăng nhập sai cho email tương ứng.
     * Được gọi sau khi đăng nhập thành công để khôi phục trạng thái ban đầu.
     */
    private void clearFailAttempts(String email) {
        redisService.delete(RedisKeyConstants.SIGN_IN_LOCK + email);
        redisService.delete(RedisKeyConstants.LOCK_FOREVER + email);
    }

    private String generateTempPassword(){
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }
}
