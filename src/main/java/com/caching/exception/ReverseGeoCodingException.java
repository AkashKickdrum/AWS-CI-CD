package com.caching.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for reverse geocoding-related errors.
 */
public class ReverseGeoCodingException extends BaseGeoCodingException {

    public ReverseGeoCodingException(String message, HttpStatus status) {
        super(message, status);
    }
    public ReverseGeoCodingException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
}