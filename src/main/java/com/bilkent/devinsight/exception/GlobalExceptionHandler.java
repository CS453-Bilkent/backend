package com.bilkent.devinsight.exception;

import com.bilkent.devinsight.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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



    private ResponseEntity<ApiResponse<Object>> createErrorResponse(HttpStatus httpStatus, String errorMessage) {
        return ResponseEntity
                .status(httpStatus)
                .body(createErrorApiResponse(httpStatus.value(), errorMessage));
    }

    private ApiResponse<Object> createErrorApiResponse(int errorCode, String errorMessage) {
        return ApiResponse.builder()
                .operationResult(ApiResponse.OperationResult.builder()
                        .returnCode(Integer.toString(errorCode))
                        .returnMessage(errorMessage)
                        .returnErrors(null)
                        .build()
                )
                .operationResultData(null)
                .build();
    }

    private ApiResponse<Object> createErrorApiResponseWithList(int errorCode, String errorMessage, List<String> errors) {
        return ApiResponse.builder()
                .operationResult(ApiResponse.OperationResult.builder()
                        .returnCode(Integer.toString(errorCode))
                        .returnMessage(errorMessage)
                        .returnErrors(errors)
                        .build()
                )
                .operationResultData(null)
                .build();
    }
}
