package com.movieproject.domain.user.dto.response;


import com.movieproject.domain.user.entity.User;

public record UserResponseDto(
        Long userId,
        String username,
        String email,
        String role // String 타입 유지
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}