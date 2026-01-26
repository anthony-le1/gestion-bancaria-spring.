package com.sistemabancario.sistemaBancario.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cuentas")
public class CuentaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;
    @Column(name = "numero_cuenta")
    private String numeroCuenta;
    private String tipoCuenta; //(Ahorro/Corriente)
    private BigDecimal saldo;
    private LocalDate fechaApertura;
    private String estado; //(ACTIVA/INACTIVA)
    private String Telefono;
    private String pin;
    private String Correo;
    @Column(name = "password")
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}