package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class InvalidResetPasswordCodeException extends BaseException {

    public InvalidResetPasswordCodeException() {
        super("Invalid reset password code!", HttpStatus.BAD_REQUEST);
    }

    public InvalidResetPasswordCodeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidResetPasswordCodeException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public InvalidResetPasswordCodeException(HttpStatus httpStatus) {
        super("Invalid reset password code!", httpStatus);
    }
}
