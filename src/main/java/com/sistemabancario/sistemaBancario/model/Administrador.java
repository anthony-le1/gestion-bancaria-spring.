package com.sistemabancario.sistemaBancario.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrador extends Usuario {

    private String codigoEmpleado;
    private String area;

    @Override
    public String obtenerTipoAcceso() {
        return "ACCESO_ADMINISTRADOR_TOTAL";
    }
}
