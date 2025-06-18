package com.thoughtNest.backend.exception;

import com.thoughtNest.backend.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all global exceptions in a consistent and centralized manner.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors triggered by @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseHandler.error("Validation failed: " + validationErrors.toString(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle general authentication failures.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthException(AuthenticationException ex) {
        return ResponseHandler.error("Authentication failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle bad credentials specifically (e.g., wrong password).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        return ResponseHandler.error("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle illegal arguments thrown in business logic.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseHandler.error("Bad request: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Catch-all handler for unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return ResponseHandler.error("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
