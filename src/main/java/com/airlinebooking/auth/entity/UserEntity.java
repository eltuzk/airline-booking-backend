package com.airlinebooking.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String passwordHash;
    private String fullName;
    private String firstName;
    private  String lastName;
    private String phoneNumber;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    /** Đặt thành true khi tài khoản bị khóa vĩnh viễn do đăng nhập sai nhiều lần liên tiếp. */
    private Boolean permanentlyLocked;

    /** Thời điểm tài khoản bị khóa vĩnh viễn. */
    private LocalDateTime permanentlyLockedAt;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
}
