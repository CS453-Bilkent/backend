package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyVerifiedException extends BaseException {

    public EmailAlreadyVerifiedException() {
        super("Email address is already verified!", HttpStatus.BAD_REQUEST);
    }

    public EmailAlreadyVerifiedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public EmailAlreadyVerifiedException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public EmailAlreadyVerifiedException(HttpStatus httpStatus) {
        super("Email address is already verified!", httpStatus);
    }
}
