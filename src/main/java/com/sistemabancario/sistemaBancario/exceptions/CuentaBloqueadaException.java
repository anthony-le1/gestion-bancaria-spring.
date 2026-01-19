package com.sistemabancario.sistemaBancario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class CuentaBloqueadaException extends RuntimeException {
    public CuentaBloqueadaException(String mensaje) { super(mensaje); }
}
