package com.hospital.exception;

/**
 * Exception thrown when user input fails server-side validation criteria.
 */
public class ValidationException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
