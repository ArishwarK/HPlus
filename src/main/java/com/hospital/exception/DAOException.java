package com.hospital.exception;

/**
 * Base unchecked exception for database access and JDBC failures.
 */
public class DAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
