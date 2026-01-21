package com.sistemabancario.sistemaBancario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Error de saldo
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> manejarSaldo(SaldoInsuficienteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 2. Atrapa cuando una cuenta no existe
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(ResourceNotFoundException ex) {
        ErrorResponse errorRes = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                "Asegúrese de que el ID o número de cuenta sea correcto",
                null
        );
        return new ResponseEntity<>(errorRes, HttpStatus.NOT_FOUND);
    }
    //Errores de validacion
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = ((FieldError) error).getDefaultMessage();
            errores.put(campo, mensaje);
        });

        ErrorResponse errorRes = new ErrorResponse(
                LocalDateTime.now(),
                "Datos invàlidos en la solicitud",
                ex.getMessage(),
                errores
        );
        return new ResponseEntity<>(errorRes.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarError(Exception ex) {
        ErrorResponse errorRes = new ErrorResponse(
                LocalDateTime.now(),
                "Ocurriò un error interno en el servidor",
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
