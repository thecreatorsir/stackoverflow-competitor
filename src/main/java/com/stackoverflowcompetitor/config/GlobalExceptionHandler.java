package com.stackoverflowcompetitor.config;

import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handlePersistenceException(PersistenceException ex) {
        log.error("PersistenceException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database operation failed: " + ex.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Constraint violation: " + ex.getMessage());
    }


    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.error("DataAccessException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data access error: " + ex.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());
        BindingResult result = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        result.getFieldErrors().forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<String>> handleBindExceptions(BindException ex) {
        log.error("BindException: {}", ex.getMessage());
        BindingResult result = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        result.getFieldErrors().forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        log.error("ResponseStatusException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
}
