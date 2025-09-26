package com.movieproject.domain.search.exception;

import com.movieproject.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SearchErrorCode implements ErrorCode {
    INVALID_KEYWORD(HttpStatus.BAD_REQUEST, "검색어는 null이거나 공백이면 안됩니다."),;

    private final HttpStatus httpStatus;
    private final String message;

    SearchErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}