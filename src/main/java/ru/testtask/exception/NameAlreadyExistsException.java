package ru.testtask.exception;

public class NameAlreadyExistsException extends RuntimeException {
    public NameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameAlreadyExistsException(String message) {
        super(message);
    }
}
