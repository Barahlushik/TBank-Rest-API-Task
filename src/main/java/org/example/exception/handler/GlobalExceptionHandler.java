package org.example.exception.handler;

import org.example.exception.SourceLanguageNotFoundException;
import org.example.exception.TargetLanguageNotFoundException;
import org.example.model.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SourceLanguageNotFoundException.class)
    public ResponseEntity<ErrorMessage>  handleGenericException(SourceLanguageNotFoundException ex) {
        logger.warn(ex.getMessage());
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Не найден поддерживаемый исходный язык"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TargetLanguageNotFoundException.class)
    public ResponseEntity<ErrorMessage>  handleGenericException(TargetLanguageNotFoundException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Не найден поддерживаемый целевой язык"), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex) {
        logger.error("Internal server error: ", ex);
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так..."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

