package com.movieproject.domain.user.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.domain.user.dto.request.LoginRequestDto;
import com.movieproject.domain.user.dto.request.PasswordChangeDto;
import com.movieproject.domain.user.dto.request.UserRequestDto;
import com.movieproject.domain.user.dto.request.UserUpdateDto;
import com.movieproject.domain.user.dto.response.LoginResponseDto;
import com.movieproject.domain.user.dto.response.UserResponseDto;
import com.movieproject.domain.user.service.external.UserExternalService;
import com.movieproject.domain.user.service.internal.UserInternalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserInternalService userInternalService;
    private final UserExternalService userExternalService;

    // 회원가입
    @PostMapping("/api/auth/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto response = userInternalService.signup(dto);
        return ApiResponse.created(response, "회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto dto) {
        LoginResponseDto response = userInternalService.login(dto);
        return ApiResponse.success(response, "로그인이 완료되었습니다.");
    }

    // 토큰 갱신
    @PostMapping("/api/auth/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");
        LoginResponseDto response = userInternalService.refreshToken(token);
        return ApiResponse.success(response, "토큰이 갱신되었습니다.");
    }

    // 로그아웃
    @PostMapping("/api/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = extractToken(request);
        userInternalService.logout(token);
        return ApiResponse.deleteSuccess("로그아웃이 완료되었습니다.");
    }

    // 내 정보 조회
    @GetMapping("/api/users/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyInfo(HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        UserResponseDto response = userInternalService.getMyInfo(userId);
        return ApiResponse.success(response, "내 정보 조회가 완료되었습니다.");
    }

    // 내 정보 수정
    @PutMapping("/api/users/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateMyInfo(HttpServletRequest request, @Valid @RequestBody UserUpdateDto dto) {
        Long userId = extractUserIdFromToken(request);
        UserResponseDto response = userInternalService.updateMyInfo(userId, dto);
        return ApiResponse.success(response, "내 정보 수정이 완료되었습니다.");
    }

    // 비밀번호 변경
    @PutMapping("/api/auth/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(HttpServletRequest request, @Valid @RequestBody PasswordChangeDto dto) {
        Long userId = extractUserIdFromToken(request);
        userInternalService.changePassword(userId, dto);
        return ApiResponse.deleteSuccess("비밀번호가 변경되었습니다.");
    }

    // 다른 사용자 조회
    @GetMapping("/api/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserInfo(@PathVariable Long userId) {
        UserResponseDto response = userExternalService.getUserInfo(userId);
        return ApiResponse.success(response, "사용자 정보 조회가 완료되었습니다.");
    }

    // 사용자명으로 조회
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUsername(@PathVariable String username) {
        UserResponseDto response = userExternalService.getUserByUsername(username);
        return ApiResponse.success(response, "사용자 정보 조회가 완료되었습니다.");
    }

    // 회원 탈퇴
    @DeleteMapping("/api/users/withdraw")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(HttpServletRequest request, @RequestBody String password) {
        Long userId = extractUserIdFromToken(request);
        userInternalService.deleteAccount(userId, password);
        return ApiResponse.deleteSuccess("회원 탈퇴가 완료되었습니다.");
    }

    //토큰에서 사용자 ID 추출하는 메서드
    private Long extractUserIdFromToken(HttpServletRequest request) {
        // TODO: JWT 토큰에서 사용자 ID 추출 로직 구현
        return 1L;
    }

    // Authorization 헤더에서 토큰 추출하는 유틸리티 메서드
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}