package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("idopont", LocalDateTime.now());
        errorBody.put("hiba", ex.getMessage());
        errorBody.put("statusz", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }
}