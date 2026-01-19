package com.sistemabancario.sistemaBancario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SaldoInsuficienteException extends BancarioException {
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
