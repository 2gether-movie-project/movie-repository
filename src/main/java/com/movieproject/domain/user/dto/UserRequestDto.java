package com.movieproject.domain.user.dto;

import com.movieproject.domain.user.entity.Role;
import com.movieproject.domain.user.entity.User;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class UserRequestDto {

    @NotBlank(message = "사용자명을 입력해주세요")
    private String username;

    @Email(message = "올바른 이메일 형식을 입력해주세요")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @NotBlank(message = "돌아가라, 나는 SKT처럼 만만하지 않아!")
    @Size(min = 8, message = "내가 KT인줄 아세요?")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).+$",
            message = "그렇다고 LG가 좋다는건 절대 아님.")
    private String password;

    @NotNull(message = "역할을 선택해주세요")
    private Role role;

    // Entity로 변환하는 메서드
    public User toEntity() {
        return User.builder()
                .username(username)
                .email(email)
                .password(password) // 실제로는 암호화된 패스워드 사용
                .role(role)
                .build();
    }
}