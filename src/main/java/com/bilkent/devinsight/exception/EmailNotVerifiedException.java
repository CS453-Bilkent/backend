package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class EmailNotVerifiedException extends BaseException {

    public EmailNotVerifiedException() {
        super("Email address not verified!", HttpStatus.BAD_REQUEST);
    }

    public EmailNotVerifiedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public EmailNotVerifiedException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public EmailNotVerifiedException(HttpStatus httpStatus) {
        super("Email address not verified!", httpStatus);
    }
}
