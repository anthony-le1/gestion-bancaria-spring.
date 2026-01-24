package com.sistemabancario.sistemaBancario.controller;

import com.sistemabancario.sistemaBancario.dto.CuentaBancariaDTO;
import com.sistemabancario.sistemaBancario.exceptions.DatosInvalidosException;
import com.sistemabancario.sistemaBancario.service.ICuentaBancariaService;
import com.sistemabancario.sistemaBancario.service.estadoCuentaPDF;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor //Inyeccion de dependencias por constructor via lombook
    @CrossOrigin(origins = "*") //Permite que React (frontend) de conecte al BackEnd
public class   CuentaBancariaController {
    //Inyeccion de dependencias
    private final ICuentaBancariaService cuentaBancariaService;

    @Autowired
    private estadoCuentaPDF estadoCuentaPDF;



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

    //buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<CuentaBancariaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaBancariaService.buscarPorId(id));
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
    @PostMapping
    public ResponseEntity <CuentaBancariaDTO> crearCuenta(@Valid @RequestBody CuentaBancariaDTO cuentaDTO) {
        CuentaBancariaDTO nuevaCuenta =  cuentaBancariaService.crearCuenta(cuentaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
    }

    @GetMapping
    public ResponseEntity<List<CuentaBancariaDTO>> listarCuentas() {
        return ResponseEntity.ok(cuentaBancariaService.listarTodas());
    }

    //EndPoint para activar o desactivar una cuenta.
    @PatchMapping("/{numeroCuenta}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable String numeroCuenta, @RequestParam String nuevoEstado) {
        if (!nuevoEstado.equals("ACTIVA") && !nuevoEstado.equals("INACTIVA")) {
            throw new DatosInvalidosException("El estado '" + nuevoEstado + "' no es válido. Use ACTIVA o INACTIVA.");
        }

        cuentaBancariaService.cambiarEstado(numeroCuenta, nuevoEstado);
        return ResponseEntity.ok("Estado actualizado");
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaBancariaDTO> actualizarDatos(@PathVariable String numeroCuenta, @RequestBody CuentaBancariaDTO datosActualizados) {
        CuentaBancariaDTO cuentaEditada = cuentaBancariaService.actualizarDatosPersonales(numeroCuenta, datosActualizados);
        return ResponseEntity.ok(cuentaEditada);
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<String> eliminarCuenta (@PathVariable String numeroCuenta) {
        cuentaBancariaService.eliminarCuenta(numeroCuenta);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String cedula = credentials.get("cedula");
        String password = credentials.get("password");

        //Admin Quemado por seguridad
        if ("9999999999".equals(cedula) && "admin123".equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "rol", "ADMIN",
                    "nombre", "ADMINISTRADOR CENTRAL"
            ));
        }

        CuentaBancariaDTO cliente = cuentaBancariaService.autenticarCliente(cedula, password);

        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @Autowired
    private estadoCuentaPDF pdfService;
    @GetMapping("/exportar-pdf/{numeroCuenta}")
    public void descargarPdf(HttpServletResponse response, @PathVariable String numeroCuenta) throws IOException {
        response.setContentType("application/pdf");
        CuentaBancariaDTO cuenta = cuentaBancariaService.consultarSaldo(numeroCuenta);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Estado_Cuenta_" + numeroCuenta + ".pdf";
        response.setHeader(headerKey, headerValue);

        pdfService.exportar(
                response,
                cuenta.getNombreCliente(),
                cuenta.getNumeroCuenta(),
                cuenta.getSaldo(),
                cuenta.getEstado(),
                cuenta.getDireccion(),
                cuenta.getCorreo()
        );
    }
}
