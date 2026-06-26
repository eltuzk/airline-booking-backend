package com.airlinebooking.auth.mapper;

import com.airlinebooking.auth.entity.UserEntity;
import com.airlinebooking.common.dto.UserDTO;

public class UserMapper {
    public static UserDTO mapToDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setUserId(userEntity.getUserId());
        if(userEntity.getRole() != null) {
        userDTO.setRole(userEntity.getRole().getRoleName());
        }
        return userDTO;

    }
}
