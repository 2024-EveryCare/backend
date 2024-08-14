package com.everycare.backend.global.exception;

import com.everycare.backend.global.common.ErrorCode;
import com.everycare.backend.global.common.RestApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        RestApiResponse errorResponse = new RestApiResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestApiResponse> handleRuntimeException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        RestApiResponse errorResponse = new RestApiResponse(errorCode);
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<RestApiResponse> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        ErrorCode errorCode = e.getErrorCode();
        RestApiResponse errorResponse = RestApiResponse.of(ErrorCode.UNAUTHORIZED_USER);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}