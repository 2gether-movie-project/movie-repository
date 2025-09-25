package com.movieproject.domain.user.dto.response;

public record UserInReviewResponseDto(
        Long userId,
        String username
) {
    public static UserInReviewResponseDto of(Long userId, String username) {
        return new UserInReviewResponseDto(userId, username);g
    }
}
