package com.sistemabancario.sistemaBancario.controller;

import com.sistemabancario.sistemaBancario.dto.CuentaBancariaDTO;
import com.sistemabancario.sistemaBancario.service.ICuentaBancariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController //Define que es un controlador
@RequestMapping("/api/cuentas") //URL
@RequiredArgsConstructor //Inyeccion de dependencias por constructor via lombook
@CrossOrigin(origins = "*") //Permite que React (frontend) de conecte al BackEnd
public class CuentaBancariaController {
    //Inyeccion de dependencias
    private final ICuentaBancariaService cuentaBancariaService;

    //Endpoint para consultar saldo (GET)
    @GetMapping("/saldo/{numeroCuenta}")
    public ResponseEntity<CuentaBancariaDTO> obtenerSaldo(@PathVariable String numeroCuenta) {
        // Enviamos el DTO completo con toda la información procesada por el Mapper
        return ResponseEntity.ok(cuentaBancariaService.consultarSaldo(numeroCuenta));
    }

    //Endpoint para depositar (POST)
    @PostMapping("/deposito")
    public ResponseEntity<String> depositar(@RequestBody Map<String, Object> payload) {
        String numero = (String) payload.get("numeroCuenta");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());

        cuentaBancariaService.depositar(numero, monto);
        return ResponseEntity.ok("Depòsito realizado con exito"); //Devolvemos un mensaje 200 ok
    }

    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestBody Map<String, Object> payload) {
        String origen = (String) payload.get("cuentaOrigen");
        String destino = (String) payload.get("cuentaDestino");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());

        cuentaBancariaService.transferir(origen, destino, monto);
        return ResponseEntity.ok("Transferencia realizada con exito");
    }

    //Endpoint para que el admin. cree una nueva cuenta
    @PostMapping("/crear")
    public ResponseEntity <CuentaBancariaDTO> crearCuenta(@RequestBody CuentaBancariaDTO cuentaDTO) {
        CuentaBancariaDTO nuevaCuenta =  cuentaBancariaService.crearCuenta(cuentaDTO);
        return ResponseEntity.ok(nuevaCuenta);
    }

    @GetMapping("/todas")
    public ResponseEntity<List<CuentaBancariaDTO>> listarCuentas() {
        return ResponseEntity.ok(cuentaBancariaService.listarTodas());
    }
}
