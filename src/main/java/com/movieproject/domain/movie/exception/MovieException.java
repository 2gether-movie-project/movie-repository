package com.movieproject.domain.movie.exception;

import com.movieproject.common.exception.ErrorCode;
import com.movieproject.common.exception.GlobalException;

public class MovieException extends GlobalException {
    public MovieException(ErrorCode errorCode) {
        super(errorCode);
    }
}
