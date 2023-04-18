package com.ummar.inventorymanagement.exceptionhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GenericHttpResponseEntity<T> {
    public ResponseEntity<String> createResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }
}