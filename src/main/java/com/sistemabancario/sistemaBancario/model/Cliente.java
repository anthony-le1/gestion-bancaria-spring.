package com.sistemabancario.sistemaBancario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clientes")
public class Cliente extends Usuario {

    private String telefono;
    private String correo;
    @Override
    public String obtenerTipoAcceso() {

        return "ACCESO_CLIENTE";
    }
}
