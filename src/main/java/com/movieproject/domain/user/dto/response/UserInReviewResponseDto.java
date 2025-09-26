package com.movieproject.domain.user.dto.response;

import com.movieproject.domain.user.entity.User;

public record UserInReviewResponseDto(
        Long userId,
        String username
) {
    public static UserInReviewResponseDto of(Long userId, String username) {
        return new UserInReviewResponseDto(userId, username);
    }

    public static UserInReviewResponseDto from(User user) {
        return new UserInReviewResponseDto(user.getUserId(), user.getUsername());
    }
}
