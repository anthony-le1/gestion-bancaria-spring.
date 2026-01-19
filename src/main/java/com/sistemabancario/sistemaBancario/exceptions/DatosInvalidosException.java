package com.sistemabancario.sistemaBancario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DatosInvalidosException extends RuntimeException {
  public DatosInvalidosException(String mensaje) { super(mensaje); }
}
