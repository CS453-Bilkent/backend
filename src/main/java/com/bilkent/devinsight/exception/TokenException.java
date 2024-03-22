package com.bilkent.devinsight.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenException extends BaseException {

    public TokenException() {
        super("Token is not valid!", HttpStatus.UNAUTHORIZED);
    }

    public TokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public TokenException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
