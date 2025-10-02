package com.movieproject.domain.director.exception;

import com.movieproject.common.exception.ErrorCode;
import com.movieproject.common.exception.GlobalException;

public class DirectorException extends GlobalException {
    public DirectorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
