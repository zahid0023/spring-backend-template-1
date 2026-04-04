package com.example.springbackendtemplate1.commons.exception;

import com.example.springbackendtemplate1.commons.dto.response.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        String rootMessage = extractRootCauseMessage(ex);

        log.warn("Data integrity violation: {}", rootMessage);

        ApiErrorResponse response = new ApiErrorResponse(
                request.getHeader("X-Request-Id"),
                HttpStatus.CONFLICT.value(),
                "DATA_INTEGRITY_VIOLATION",
                rootMessage
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return build(
                ex,
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                request
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {

        log.warn("Entity not found: {}", ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
                request.getHeader("X-Request-Id"),
                HttpStatus.NOT_FOUND.value(),
                "ENTITY_NOT_FOUND",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleUnauthorized(
            AuthorizationDeniedException ex,
            HttpServletRequest request
    ) {
        log.error("Unauthorized access attempt: {}", ex.getMessage());
        log.error("Unauthorized access attempt: {}", ex.getAuthorizationResult());

        return build(
                ex,
                HttpStatus.FORBIDDEN,
                "UNAUTHORIZED",
                request
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unexpected error", ex);

        return build(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                request
        );
    }

    private ResponseEntity<@NonNull ApiErrorResponse> build(
            Exception ex,
            HttpStatus status,
            String code,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                request.getHeader("X-Request-Id"),
                status.value(),
                code,
                ex.getMessage()
        );
        return ResponseEntity.status(status).body(response);
    }

    private String extractRootCauseMessage(Throwable ex) {
        Throwable root = ex;

        while (root.getCause() != null) {
            root = root.getCause();
        }

        return root.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return build(
                ex,
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                request
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleDisabledAccount(
            DisabledException ex,
            HttpServletRequest request
    ) {
        return build(
                ex,
                HttpStatus.FORBIDDEN,
                "ACCOUNT_DISABLED",
                request
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleLockedAccount(
            LockedException ex,
            HttpServletRequest request
    ) {
        return build(
                ex,
                HttpStatus.LOCKED,
                "ACCOUNT_LOCKED",
                request
        );
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<@NonNull ApiErrorResponse> handleExpiredAccount(
            AccountExpiredException ex,
            HttpServletRequest request
    ) {
        return build(
                ex,
                HttpStatus.FORBIDDEN,
                "ACCOUNT_EXPIRED",
                request
        );
    }

}
