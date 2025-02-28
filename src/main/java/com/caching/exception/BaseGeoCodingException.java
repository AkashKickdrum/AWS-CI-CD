package com.caching.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for geocoding-related exceptions with HTTP status support.
 */
public abstract class BaseGeoCodingException extends RuntimeException {

    private final HttpStatus status;
    protected BaseGeoCodingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    protected BaseGeoCodingException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
    public HttpStatus getStatus() {
        return status;
    }
}
