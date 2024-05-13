package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends BaseException {

    public RepositoryNotFoundException() {
        super("Repository not found!", HttpStatus.NOT_FOUND);
    }

    public RepositoryNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public RepositoryNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public RepositoryNotFoundException(HttpStatus httpStatus) {
        super("Repository not found!", httpStatus);
    }
}
