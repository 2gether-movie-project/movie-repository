package com.movieproject.domain.user.dto.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
    public static LoginResponseDto of(String accessToken, String refreshToken) {
        return new LoginResponseDto(accessToken, refreshToken);
    }
}