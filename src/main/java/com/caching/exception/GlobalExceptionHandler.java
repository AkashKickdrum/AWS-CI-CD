package com.caching.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type BaseGeoCodingException.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(BaseGeoCodingException.class)
    public ResponseEntity<Map<String, Object>> handleBaseGeoCodingException(BaseGeoCodingException ex) {
        return buildErrorResponse(ex.getMessage(), ex.getStatus());
    }

    /**
     * Handles exceptions of type MethodArgumentTypeMismatchException.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions of type MethodArgumentTypeMismatchException.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String message = String.format("Invalid value for parameter '%s'. Expected type: %s",
                parameterName, Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Builds an error response with the specified message and status.
     *
     * @param message the error message
     * @param status  the HTTP status
     * @return a ResponseEntity containing the error details
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        return new ResponseEntity<>(errorDetails, status);
    }
}
