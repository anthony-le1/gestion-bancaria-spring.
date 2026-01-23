package com.sistemabancario.sistemaBancario.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancariaDTO {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "Debe ser mayor de 18 años")
    @Max(value = 120, message = "Edad no válida")
    private Integer edad;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private String numeroCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @PositiveOrZero(message = "El saldo no puede ser negativo ")
    @DecimalMin(value = "0.0", message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    private String tipoCuenta; //Ahorros, corriente
    private String estado; //Activa, Inactiva

    @NotBlank(message = "El numero telefònico es obligatorio")
    private String telefono;

    @NotBlank(message = "Ingrese la contraseña")
    @NotNull
    private String password;

    @NotBlank(message = "El correo electrònico es obligatorio")
    public String correo;

    @NotBlank(message = "La cèdula es obligatoria")
    public String cedula;

    @NotBlank(message = "El rol es obligatorio")
    public String rol;
}
