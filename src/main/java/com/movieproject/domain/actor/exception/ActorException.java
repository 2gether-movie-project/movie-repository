package com.movieproject.domain.actor.exception;

import com.movieproject.common.exception.ErrorCode;
import com.movieproject.common.exception.GlobalException;

public class ActorException extends GlobalException {
    public ActorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
