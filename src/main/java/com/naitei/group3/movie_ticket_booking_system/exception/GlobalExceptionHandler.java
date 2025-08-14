package com.naitei.group3.movie_ticket_booking_system.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import com.naitei.group3.movie_ticket_booking_system.dto.response.BaseApiResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    private <T> BaseApiResponse<T> buildErrorResponse(HttpStatus status, String message) {
        return new BaseApiResponse<>(status.value(), message);
    }

    private ModelAndView buildErrorPage(int statusCode, String title, String message,
                                        String redirectUrl, String buttonLabel) {
        ModelAndView mv = new ModelAndView("errors/error");
        mv.addObject("statusCode", statusCode);
        mv.addObject("title", title);
        mv.addObject("errorMessage", message);
        mv.addObject("redirectUrl", redirectUrl);
        mv.addObject("buttonLabel", buttonLabel);
        return mv;
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
    public Object handleGenericException(Exception ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                            ex.getMessage() != null ? ex.getMessage() : getMessage("error.internal.mess", null)));
        }
        return buildErrorPage(
                500,
                getMessage("error.internal.title", null),
                ex.getMessage() != null ? ex.getMessage() : getMessage("error.internal.mess", null),
                "/admin",
                getMessage("button.back.home", null)
        );
    }

    // 400 - Bad request - req không hợp lệ
    @ExceptionHandler(BadRequestException.class)
    public Object handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
        }
        return buildErrorPage(
                400,
                getMessage("error.badrequest.title", null),
                getMessage("error.badrequest.mess", null),
                "/admin",
                getMessage("button.back.home", null)
        );
    }

    // 403 - Access denied - thiếu quyền truy cập
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Object handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse(HttpStatus.FORBIDDEN,
                            getMessage("error.accessdenied.mess", null)));
        }
        return buildErrorPage(
                403,
                getMessage("error.accessdenied.title", null),
                getMessage("error.accessdenied.mess", null),
                "/login",
                getMessage("button.back.login", null)
        );
    }

    // 401 - Authentication failed - lỗi xác thực
    @ExceptionHandler(AuthenticationFailedEx.class)
    public Object handleAuthFailed(AuthenticationFailedEx ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(buildErrorResponse(HttpStatus.UNAUTHORIZED,
                            ex.getMessage() != null ? ex.getMessage() : getMessage("error.authenticationfailed.mess", null)));
        }
        return buildErrorPage(
                401,
                getMessage("error.authenticationfailed.title", null),
                ex.getMessage() != null ? ex.getMessage() : getMessage("error.authenticationfailed.mess", null),
                "/login",
                getMessage("button.back.login", null)
        );
    }

    // 401 - Bad credentials - lỗi đăng nhập
    @ExceptionHandler(BadCredentialsException.class)
    public Object handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()));
        }
        return buildErrorPage(
                401,
                getMessage("error.badcredentials.title", null),
                getMessage("error.badcredentials.mess", null),
                "/login",
                getMessage("button.back.login", null)
        );
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

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFound(RoleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
        }
        return buildErrorPage(
                404,
                getMessage("error.notfound.title", null),
                getMessage("error.notfound.mess", null),
                "/admin",
                getMessage("button.back.home", null)
        );
    }
}
