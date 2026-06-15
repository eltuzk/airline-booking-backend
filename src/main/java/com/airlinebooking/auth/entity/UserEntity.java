package com.airlinebooking.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "users")
@Getter
@Setter
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
}
