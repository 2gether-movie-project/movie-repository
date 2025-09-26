package com.movieproject.domain.user.entity;

import lombok.Getter;

@Getter
public enum Role {
    USER("회원"),
    ADMIN("관리자");

    private String role;

    Role(String role) {
        this.role = role;
    }
}