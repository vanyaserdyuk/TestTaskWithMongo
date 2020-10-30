package ru.testtask.exception;

public class WrongMethodUseException extends RuntimeException {
    public WrongMethodUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongMethodUseException(String message) {
        super(message);
    }
}
