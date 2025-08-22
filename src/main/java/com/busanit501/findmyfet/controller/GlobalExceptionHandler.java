package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse response = ErrorResponse.of("BAD_REQUEST", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 다른 종류의 예외들을 처리하는 핸들러를 여기에 추가할 수 있습니다.
    // @ExceptionHandler(CustomException.class)
    // public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) { ... }
}
