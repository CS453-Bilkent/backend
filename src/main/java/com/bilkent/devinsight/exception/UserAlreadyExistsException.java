package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {


    public UserAlreadyExistsException() {
        super("This user already exists!", HttpStatus.BAD_REQUEST);
    }

    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
