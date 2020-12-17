package ru.testtask.exception;

public class FileIsToLargeException extends RuntimeException {
    public FileIsToLargeException(String message) {
        super(message);
    }
}
