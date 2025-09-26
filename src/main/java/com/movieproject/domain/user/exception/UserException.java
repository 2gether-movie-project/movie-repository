package com.movieproject.domain.user.exception;

import com.movieproject.common.exception.GlobalException;

public class UserException extends GlobalException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}