package com.airlinebooking.common.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCache {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String fullName;

    private String role;

    private String phone;


}
