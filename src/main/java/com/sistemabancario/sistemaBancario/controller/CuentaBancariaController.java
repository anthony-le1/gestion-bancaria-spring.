package com.sistemabancario.sistemaBancario.controller;

import com.sistemabancario.sistemaBancario.dto.CuentaBancariaDTO;
import com.sistemabancario.sistemaBancario.service.ICuentaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor //Inyeccion de dependencias por constructor via lombook
@CrossOrigin(origins = "*") //Permite que React (frontend) de conecte al BackEnd
public class CuentaBancariaController {
    //Inyeccion de dependencias
    private final ICuentaBancariaService cuentaBancariaService;

    //Endpoint para consultar sald o (GET)
    @GetMapping("/saldo/{numeroCuenta}")
    public ResponseEntity<CuentaBancariaDTO> obtenerSaldo(@PathVariable String numeroCuenta) {
        // Enviamos el DTO completo con toda la información procesada por el Mapper
        return ResponseEntity.ok(cuentaBancariaService.consultarSaldo(numeroCuenta));
    }

    //Endpoint para depositar (POST)
    @PostMapping("/deposito")
    public ResponseEntity<String> depositar(@Valid @RequestBody Map<String, Object> payload) {
        String numero = (String) payload.get("numeroCuenta");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());

        cuentaBancariaService.depositar(numero, monto);
        return ResponseEntity.ok("Depòsito realizado con exito"); //Devolvemos un mensaje 200 ok
    }

    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@Valid @RequestBody Map<String, Object> payload) {
        String origen = (String) payload.get("cuentaOrigen");
        String destino = (String) payload.get("cuentaDestino");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());

        cuentaBancariaService.transferir(origen, destino, monto);
        return ResponseEntity.ok("Transferencia realizada con exito");
    }

    //Endpoint para que el admin. cree una nueva cuenta
    @PostMapping("/crear")
    public ResponseEntity <CuentaBancariaDTO> crearCuenta(@Valid @RequestBody CuentaBancariaDTO cuentaDTO) {
        CuentaBancariaDTO nuevaCuenta =  cuentaBancariaService.crearCuenta(cuentaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
    }

    @GetMapping("/todas")
    public ResponseEntity<List<CuentaBancariaDTO>> listarCuentas() {
        return ResponseEntity.ok(cuentaBancariaService.listarTodas());
    }

    //EndPoint para activar o desactivar una cuenta.
    @PatchMapping("/{numeroCuenta}/estado")
    public ResponseEntity<String> cambiarEstado(@Valid @PathVariable String numeroCuenta, @RequestBody Map<String, Object> payload) {
        String estado = payload.get("estado").toString();
        cuentaBancariaService.cambiarEstado(numeroCuenta, estado);
        return ResponseEntity.ok("Estado de la cuenta actualizado a: " + estado);
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> eliminarCuenta (@PathVariable String numeroCuenta) {
        cuentaBancariaService.eliminarCuenta(numeroCuenta);
        //Aqui implemnetamos un mensaje 204 para mostrar un DELETE exitoso
        return ResponseEntity.ok().build();
    }
}
