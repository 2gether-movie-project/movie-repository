package com.movieproject.domain.user.dto;

import com.movieproject.domain.user.entity.Role;
import com.movieproject.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String role;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getRole())
                .build();
    }
}