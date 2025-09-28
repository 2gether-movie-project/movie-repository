package com.movieproject.domain.cast.exception;

import com.movieproject.common.exception.ErrorCode;
import com.movieproject.common.exception.GlobalException;

public class CastException extends GlobalException {
    public CastException(ErrorCode errorCode) {
        super(errorCode);
    }
}
