package com.sistemabancario.sistemaBancario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Error de saldo
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> manejarSaldo(SaldoInsuficienteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Cuando algo no existe
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> manejarNoEncontrado(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
