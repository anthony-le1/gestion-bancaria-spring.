package com.sistemabancario.sistemaBancario.repository;

import aQute.bnd.annotation.plugin.InternalPluginNamespace;
import com.sistemabancario.sistemaBancario.model.CuentaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {
    //Buscar la cuenta por su número de cuenta de 10 digitos
    Optional<CuentaBancaria> findByNumeroCuenta(String numeroCuenta);

    boolean existsByNumeroCuenta(String numeroCuenta);
    List<CuentaBancaria> findByCliente_NombreIgnoreCase(String nombre);

    Optional<CuentaBancaria> findByClienteCedula(String cedula);
}
