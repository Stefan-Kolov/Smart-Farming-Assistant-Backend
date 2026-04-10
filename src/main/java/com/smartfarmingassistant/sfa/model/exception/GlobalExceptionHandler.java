package com.smartfarmingassistant.sfa.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handle(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<String> handle(IncorrectPasswordException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handle(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
