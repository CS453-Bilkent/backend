package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {


    public UserNotFoundException() {
        super("User not found!", HttpStatus.BAD_REQUEST);
    }

    public UserNotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public UserNotFoundException(HttpStatus httpStatus) {
        super("User not found!", httpStatus);
    }

}
