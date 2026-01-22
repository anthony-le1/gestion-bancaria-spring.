package com.sistemabancario.sistemaBancario.service;

import com.sistemabancario.sistemaBancario.dto.CuentaBancariaDTO;
import com.sistemabancario.sistemaBancario.model.CuentaBancaria;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface ICuentaBancariaService {
    CuentaBancariaDTO crearCuenta(CuentaBancariaDTO cuentaDTO);

    CuentaBancariaDTO consultarSaldo(String numeroCuenta);
    void depositar(String numeroCuenta, BigDecimal monto);
    void retirar(String numeroCuenta, BigDecimal monto);

    List<CuentaBancariaDTO> listarTodas();

    void cambiarEstado(String numeroCuenta, String nuevoEstado);
    void eliminarCuenta(String numeroCuenta);
    @Transactional
    void transferir(String cuentaOrigen, String cuentaDestino, BigDecimal monto);
    CuentaBancariaDTO actualizarDatosPersonales(String numeroCuenta, CuentaBancariaDTO dto);
    CuentaBancariaDTO autenticarCliente(String cedula, String password);
}