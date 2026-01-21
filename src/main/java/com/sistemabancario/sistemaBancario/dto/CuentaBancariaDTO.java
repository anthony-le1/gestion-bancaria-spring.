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

    @NotBlank(message = "La edad es obligatoria")
    @Pattern(regexp = "\\d+", message = "La edad debe ser un número válido")
    @Min(value = 18, message = "Debe ser mayor de 18 años para abrir una cuenta")
    @Max(value = 120, message = "Edad no válida")
    private Integer edad;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El numero de cuenta no puede estar vacìo")
    @Size(min = 10, max = 10, message = "El numero de cuenta debe tener 10 digitos")
    private String numeroCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @PositiveOrZero(message = "El saldo no puede ser negativo ")
    @DecimalMin(value = "0.0", message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    private String tipoCuenta; //Ahorros, corriente
    private String estado; //Activa, Inactiva


}
