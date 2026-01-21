package com.sistemabancario.sistemaBancario.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String mensajes;
    private String detalles;
    private Map<String, String> erroresValidacion;  //para mostrar que campo fallò exactamente
}
