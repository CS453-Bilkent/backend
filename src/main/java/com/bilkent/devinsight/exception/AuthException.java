package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {

    public AuthException() {
        super("Invalid authentication!", HttpStatus.UNAUTHORIZED);
    }

    public AuthException(HttpStatus httpStatus) {
        super("Invalid authentication!", httpStatus);
    }

    public AuthException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }


    public AuthException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }


}
