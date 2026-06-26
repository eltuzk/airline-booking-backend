package com.airlinebooking.auth.services.imp;

import com.airlinebooking.auth.dto.request.ChangeFirstPassRequest;
import com.airlinebooking.auth.dto.request.LoginRequest;
import com.airlinebooking.auth.dto.request.RegisterRequest;
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

        String accessToken = jwtUtil.generateAccessToken(UserMapper.mapToDTO(userEntity));

        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public void logout(HttpServletRequest request) {
        String dataAuthen = request.getHeader("Authorization");
        if (dataAuthen == null || !dataAuthen.startsWith("Bearer ")) {
            throw new ResourceNotFoundException(ErrorCode.INVALID_TOKEN);
        }
        String token = dataAuthen.substring(7);
        Claims claims = jwtUtil.parseAccessToken(token);
        String userId = claims.getSubject();
        redisService.delete(RedisKeyConstants.LOGIN_SESSION + userId);
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
        RoleEntity role = roleRepository.findByRoleName("ROLE_USER").orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.INVALID_ROLE)
        );
        String dataAuthen = request.getHeader("Authorization");
        String token = dataAuthen.substring(7);
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

    /**
     * Xóa tất cả các key Redis liên quan đến đăng nhập sai cho email tương ứng.
     * Được gọi sau khi đăng nhập thành công để khôi phục trạng thái ban đầu.
     */
    private void clearFailAttempts(String email) {
        redisService.delete(RedisKeyConstants.LOCK_COUNT + email);
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
