package com.caching.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for geocoding-related errors.
 */
public class GeoCodingException extends BaseGeoCodingException {

    public GeoCodingException(String message, HttpStatus status) {
        super(message, status);
    }
    public GeoCodingException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
}