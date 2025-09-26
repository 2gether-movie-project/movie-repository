package com.movieproject.domain.review.exception;

import com.movieproject.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "리뷰를 작성한 영화입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "리뷰에 대한 권한이 없습니다."),
    REVIEW_MOVIE_MISMATCH(HttpStatus.BAD_REQUEST, "리뷰가 해당 영화에 속하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
