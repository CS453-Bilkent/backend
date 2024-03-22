package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class InvalidChangeEmailCodeException extends BaseException {

    public InvalidChangeEmailCodeException() {
        super("Invalid change email code!", HttpStatus.BAD_REQUEST);
    }

    public InvalidChangeEmailCodeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidChangeEmailCodeException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public InvalidChangeEmailCodeException(HttpStatus httpStatus) {
        super("Invalid change email code!", httpStatus);
    }
}
