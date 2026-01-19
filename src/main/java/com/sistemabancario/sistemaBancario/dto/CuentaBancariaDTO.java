package com.sistemabancario.sistemaBancario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancariaDTO {
    private String numeroCuenta;
    private BigDecimal saldo;
    private String tipoCuenta; //Ahorros, corriente
    private String estado; //Activa, Inactiva
    private String nombreCliente; //Muestra en la interfaz


}
