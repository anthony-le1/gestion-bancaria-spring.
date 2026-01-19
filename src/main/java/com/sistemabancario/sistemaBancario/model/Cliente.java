package com.sistemabancario.sistemaBancario.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {

    private String direccion;
    private String telefono;

    @Override
    public String obtenerTipoAcceso() {
        return "ACCESO_CLIENTE";
    }
}
