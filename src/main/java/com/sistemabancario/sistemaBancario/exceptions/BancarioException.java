package com.sistemabancario.sistemaBancario.exceptions;

public class BancarioException extends RuntimeException {
    public BancarioException(String mensaje) {
        super(mensaje);
    }
}
