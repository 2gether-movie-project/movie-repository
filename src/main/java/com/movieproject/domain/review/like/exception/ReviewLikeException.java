package com.movieproject.domain.review.like.exception;

import com.movieproject.common.exception.ErrorCode;
import com.movieproject.common.exception.GlobalException;

public class ReviewLikeException extends GlobalException {
    public ReviewLikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
