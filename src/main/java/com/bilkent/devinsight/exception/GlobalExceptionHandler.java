package com.bilkent.devinsight.exception;

import com.bilkent.devinsight.response.struct.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleServiceExceptions(BaseException exception) {
        log.error("An exception occurred " + exception.getMessage());

        return createErrorResponse(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExistsExceptions(UserAlreadyExistsException exception) {
        log.error("An exception occurred " + exception.getMessage());

        return createErrorResponse(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundExceptions(UserNotFoundException exception) {
        log.error("An exception occurred " + exception.getMessage());

        return createErrorResponse(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiResponse<Object>> handleWrongPasswordExceptions(WrongPasswordException exception) {
        log.error("An exception occurred " + exception.getMessage());

        return createErrorResponse(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(SomethingWentWrongException.class)
    public ResponseEntity<ApiResponse<Object>> handleWrongPasswordExceptions(SomethingWentWrongException exception) {
        log.error("An exception occurred " + exception.getMessage());

        return createErrorResponse(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(GithubConnectionException.class)
    public ResponseEntity<ApiResponse<Object>> handleWrongPasswordExceptions(GithubConnectionException exception) {
        log.error("An exception occurred " + exception.getMessage());

        return createErrorResponse(exception.getHttpStatus(), exception.getMessage());
    }


    private ResponseEntity<ApiResponse<Object>> createErrorResponse(HttpStatus httpStatus, String errorMessage) {
        return ResponseEntity
                .status(httpStatus)
                .body(createErrorApiResponse(httpStatus.value(), errorMessage));
    }

    private ApiResponse<Object> createErrorApiResponse(int errorCode, String errorMessage) {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        return ApiResponse.builder()
                .message(errorMessage)
                .timestamp(timestamp)
                .status(errorCode)
                .build();
    }

    private ApiResponse<Object> createErrorApiResponseWithList(int errorCode, String errorMessage, List<String> errors) {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        return ApiResponse.builder()
                .message(errorMessage)
                .timestamp(timestamp)
                .status(errorCode)
                .build();
    }
}
