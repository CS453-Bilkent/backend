package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class WrongPasswordException extends BaseException {


    public WrongPasswordException() {
        super("Password does not match!", HttpStatus.BAD_REQUEST);
    }

    public WrongPasswordException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
