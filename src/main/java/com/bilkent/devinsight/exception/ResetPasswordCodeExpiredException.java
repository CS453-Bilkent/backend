package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class ResetPasswordCodeExpiredException extends BaseException {

    public ResetPasswordCodeExpiredException() {
        super("Expired reset password code!", HttpStatus.BAD_REQUEST);
    }

    public ResetPasswordCodeExpiredException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ResetPasswordCodeExpiredException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public ResetPasswordCodeExpiredException(HttpStatus httpStatus) {
        super("Expired reset password code!", httpStatus);
    }
}
