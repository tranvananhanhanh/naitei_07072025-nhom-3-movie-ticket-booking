package com.naitei.group3.movie_ticket_booking_system.exception;

import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import com.naitei.group3.movie_ticket_booking_system.dto.response.BaseApiResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    private <T> BaseApiResponse<T> buildErrorResponse(HttpStatus status, String message) {
        return new BaseApiResponse<>(status.value(), message);
    }

    // ResponseStatusException ném từ controller
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaseApiResponse<?>> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity
                .status(ex.getStatusCode().value())
                .body(buildErrorResponse(status,
                        ex.getMessage()));
    }

    // 500 - fallback exception - các lỗi chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseApiResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getMessage() != null ? ex.getMessage() : "Đã xảy ra lỗi không mong muốn"));
    }

    // 400 - Bad request - req không hợp lệ
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseApiResponse<?>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // 403 - Access denied - thiếu quyền truy cập
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<BaseApiResponse<?>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(HttpStatus.FORBIDDEN,
                        "Bạn không có quyền thực hiện hành động này"));
    }

    // 401 - Authentication failed - lỗi xác thực
    @ExceptionHandler(AuthenticationFailedEx.class)
    public ResponseEntity<BaseApiResponse<?>> handleAuthFailed(AuthenticationFailedEx ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(HttpStatus.UNAUTHORIZED,
                        ex.getMessage() != null ? ex.getMessage() : "Xác thực thất bại"));
    }

    // 401 - Bad credentials - lỗi đăng nhập
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseApiResponse<?>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    // handle validation error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest()
                .body(new BaseApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseApiResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            errors.put(fieldName, violation.getMessage());
        });

        return ResponseEntity.badRequest()
                .body(new BaseApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                ));
    }
    
    // Viết thêm các handler sau này ...
    // @ExceptionHandler(ResourceNotFoundException.class)
}

