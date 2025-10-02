package com.movieproject.domain.search.exception;

import com.movieproject.common.exception.GlobalException;

public class InvalidSearchException extends GlobalException {
    public InvalidSearchException(SearchErrorCode errorCode) {
        super(errorCode);
    }
}
