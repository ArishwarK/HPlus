package com.hospital.exception;

/**
 * Exception thrown for business logic failures, state conflicts, or domain violations.
 */
public class ServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
