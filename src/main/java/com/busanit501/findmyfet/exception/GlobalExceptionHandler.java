package com.busanit501.findmyfet.exception;
import com.busanit501.findmyfet.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestController;

// 모든 컨트롤러에서 발생하는 예외를 잡아서 처리해주는 클래스
@RestController
public class GlobalExceptionHandler {

    // 특정 예외(IllegalArgumentException)를 잡아서 하나의 메소드에서 공통 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 'NOT_FOUND' 코드와 예외 메시지를 담은 ErrorResponse 객체 생성
        ErrorResponse response = new ErrorResponse("NOT_FOUND", ex.getMessage());
        // 404 (Not Found) 상태 코드와 함께 ErrorResponse를 클라이언트에게 반환
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 권한 관련 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse response = new ErrorResponse("FORBIDDEN", ex.getMessage());
        // 403 (Forbidden) 상태 코드와 함께 응답
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 향후 다른 종류의 예외가 생기면 여기에 메소드를 추가하면 됩니다.
    // @ExceptionHandler(SomeOtherException.class)
    // public ResponseEntity<ErrorResponse> handleSomeOtherException(SomeOtherException ex) { ... }
}

