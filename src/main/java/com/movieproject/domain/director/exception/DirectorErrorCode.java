package com.movieproject.domain.director.exception;

import com.movieproject.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DirectorErrorCode implements ErrorCode {

    DIRECTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 감독을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}