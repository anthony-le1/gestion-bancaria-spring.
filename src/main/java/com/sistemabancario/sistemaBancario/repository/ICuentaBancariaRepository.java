package com.sistemabancario.sistemaBancario.repository;

import com.sistemabancario.sistemaBancario.model.CuentaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ICuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {
    //Buscar la cuenta por su número de cuenta de 10 digitos
    Optional<CuentaBancaria> findByNumeroCuenta(String numeroCuenta);
}
