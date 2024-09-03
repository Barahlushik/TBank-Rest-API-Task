package org.example.exception;

public class SourceLanguageNotFoundException extends RuntimeException {
    public SourceLanguageNotFoundException(String message) {
        super(message);
    }
}
