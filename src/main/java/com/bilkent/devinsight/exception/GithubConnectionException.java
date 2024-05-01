package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class GithubConnectionException extends BaseException {

    private final static String DEFAULT_MESSAGE = "Could not connect to the Github service.";

    public GithubConnectionException() {
        super(DEFAULT_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public GithubConnectionException(HttpStatus httpStatus) {
        super(DEFAULT_MESSAGE, httpStatus);
    }

    public GithubConnectionException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public GithubConnectionException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }


}
