package com.sistemabancario.sistemaBancario.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;
    private String numeroCuenta;
    private String tipoCuenta; //(Ahorro/Corriente)
    private BigDecimal saldo;
    private LocalDate fechaApertura;
    private String estado; //(Activa/Bloqueada)
    private String moneda;
    private BigDecimal limiteDiario;
    private Double tasaInteres;
    private String pin;
    private String sucursal;
    private boolean tieneTarjeta;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}