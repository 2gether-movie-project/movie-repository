package com.movieproject.domain.actor.exception;

import com.movieproject.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ActorErrorCode implements ErrorCode {

    ALREADY_EXIST_ACTOR(HttpStatus.CONFLICT, "이미 존재하는 배우 입니다."),
    ACTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 배우를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
