package com.movieproject.domain.cast.exception;

import com.movieproject.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CastErrorCode implements ErrorCode {

    ALREADY_EXIST_CAST(HttpStatus.CONFLICT, "이미 존재하는 캐스트 입니다."),
    CAST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 캐스트를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
