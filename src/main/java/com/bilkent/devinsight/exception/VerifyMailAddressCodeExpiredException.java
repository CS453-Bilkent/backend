package com.bilkent.devinsight.exception;

import org.springframework.http.HttpStatus;

public class VerifyMailAddressCodeExpiredException extends BaseException {

    public VerifyMailAddressCodeExpiredException() {
        super("Expired verify email code!", HttpStatus.BAD_REQUEST);
    }

    public VerifyMailAddressCodeExpiredException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public VerifyMailAddressCodeExpiredException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public VerifyMailAddressCodeExpiredException(HttpStatus httpStatus) {
        super("Expired verify email code!", httpStatus);
    }
}
