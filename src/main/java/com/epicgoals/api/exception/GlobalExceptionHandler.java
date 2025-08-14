// ABOUT_ME: Global exception handler for consistent error response formatting
// ABOUT_ME: Catches exceptions across all controllers and returns standardized error responses
package com.epicgoals.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        
        Map<String, Object> error = Map.of(
                "code", "INTERNAL_SERVER_ERROR",
                "message", "An unexpected error occurred",
                "timestamp", Instant.now().toString()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> error = Map.of(
                "code", "VALIDATION_ERROR",
                "message", "Validation failed",
                "details", fieldErrors,
                "timestamp", Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> error = Map.of(
                "code", "INVALID_INPUT",
                "message", ex.getMessage(),
                "timestamp", Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> error = Map.of(
                "code", "INVALID_CREDENTIALS",
                "message", ex.getMessage(),
                "timestamp", Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", error));
    }
}