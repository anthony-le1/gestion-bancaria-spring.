package com.sistemabancario.sistemaBancario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Para que cada hijo tenga su propia tabla
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int edad;
    private String cedula;
    private String direccion;
    private String email;
    private String password;
    private String rol; // "ADMIN" o "CLIENTE"

    public abstract String obtenerTipoAcceso(); //obliga a que cada hijo defina su propia logica
}
