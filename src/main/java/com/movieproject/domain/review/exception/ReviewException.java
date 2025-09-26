package com.movieproject.domain.review.exception;

import com.movieproject.common.exception.ErrorCode;
import com.movieproject.common.exception.GlobalException;

public class ReviewException extends GlobalException {
    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }
}
