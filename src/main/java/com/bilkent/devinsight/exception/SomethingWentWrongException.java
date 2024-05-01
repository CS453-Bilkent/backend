package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class SomethingWentWrongException extends BaseException {

    private final static String DEFAULT_MESSAGE = "Something went wrong.";

    public SomethingWentWrongException() {
        super(DEFAULT_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SomethingWentWrongException(HttpStatus httpStatus) {
        super(DEFAULT_MESSAGE, httpStatus);
    }

    public SomethingWentWrongException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public SomethingWentWrongException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }


}
