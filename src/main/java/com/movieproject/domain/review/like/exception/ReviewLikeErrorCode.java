package com.movieproject.domain.review.like.exception;

import com.movieproject.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewLikeErrorCode implements ErrorCode {

    REVIEW_LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요 누른 리뷰입니다." ),
    REVIEW_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 기록을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
