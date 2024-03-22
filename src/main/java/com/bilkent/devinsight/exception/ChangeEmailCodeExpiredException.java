package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class ChangeEmailCodeExpiredException extends BaseException {

    public ChangeEmailCodeExpiredException() {
        super("Expired change email code!", HttpStatus.BAD_REQUEST);
    }

    public ChangeEmailCodeExpiredException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ChangeEmailCodeExpiredException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public ChangeEmailCodeExpiredException(HttpStatus httpStatus) {
        super("Expired change email code!", httpStatus);
    }
}
