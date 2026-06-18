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


}
