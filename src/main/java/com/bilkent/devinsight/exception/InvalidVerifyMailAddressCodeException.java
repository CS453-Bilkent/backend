package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class InvalidVerifyMailAddressCodeException extends BaseException {

    public InvalidVerifyMailAddressCodeException() {
        super("Invalid verify email code!", HttpStatus.BAD_REQUEST);
    }

    public InvalidVerifyMailAddressCodeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidVerifyMailAddressCodeException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public InvalidVerifyMailAddressCodeException(HttpStatus httpStatus) {
        super("Invalid verify email code!", httpStatus);
    }
}
