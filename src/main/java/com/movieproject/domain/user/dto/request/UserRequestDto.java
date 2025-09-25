package com.movieproject.domain.user.dto.request;

import com.movieproject.domain.user.entity.Role;
import com.movieproject.domain.user.entity.User;
import jakarta.validation.constraints.*;

public record UserRequestDto(
        @NotBlank(message = "사용자명을 입력해주세요")
        String username,

        @Email(message = "올바른 이메일 형식을 입력해주세요")
        @NotBlank(message = "이메일을 입력해주세요")
        String email,

        @NotBlank(message = "돌아가라, 나는 SKT처럼 만만하지 않아!")
        @Size(min = 8, message = "내가 KT인줄 아세요?")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$")        String password,
        @NotNull(message = "역할을 선택해주세요")
        Role role
) {
    public User toEntity() {
        return User.builder().username(username)
                .email(email)
                .password(password)
                .role(role)
                .build();
    }
}