package com.movieproject.domain.user.dto.request;

import jakarta.validation.constraints.Email;

public record UserUpdateDto(
        String username,

        @Email(message = "올바른 이메일 형식을 입력해주세요")
        String email
) {}