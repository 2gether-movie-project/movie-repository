package com.movieproject.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class UserUpdateDto {

    private String username;

    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String email;
}