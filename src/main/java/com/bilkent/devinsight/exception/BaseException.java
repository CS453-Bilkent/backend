package com.bilkent.devinsight.exception;


import org.springframework.http.HttpStatus;


public class BaseException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;

    public BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
