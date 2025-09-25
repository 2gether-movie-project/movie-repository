package com.movieproject.domain.user.dto.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
    // 롬복의 @Builder를 대체하는 팩토리 메서드 예시 (선택사항)
    public static LoginResponseDto of(String accessToken, String refreshToken) {
        return new LoginResponseDto(accessToken, refreshToken);
    }
}