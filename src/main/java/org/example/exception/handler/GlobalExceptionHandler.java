package org.example.exception.handler;

import org.example.exception.SourceLanguageNotFoundException;
import org.example.exception.TargetLanguageNotFoundException;
import org.example.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SourceLanguageNotFoundException.class)
    public ResponseEntity<ErrorMessage>  handleGenericException(SourceLanguageNotFoundException ex) {
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TargetLanguageNotFoundException.class)
    public ResponseEntity<ErrorMessage>  handleGenericException(TargetLanguageNotFoundException ex) {
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

}

