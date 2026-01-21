package com.sistemabancario.sistemaBancario.mapper;

import com.sistemabancario.sistemaBancario.dto.CuentaBancariaDTO;
import com.sistemabancario.sistemaBancario.model.Cliente;
import com.sistemabancario.sistemaBancario.model.CuentaBancaria;
import org.springframework.stereotype.Component;

@Component
public class CuentaBancariaMapper {

    //Entidad a DTO (Enviar datos al frontend)
    public CuentaBancariaDTO toDTO(CuentaBancaria cuenta){
        if (cuenta == null) return null;

        CuentaBancariaDTO dto = new CuentaBancariaDTO();
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setSaldo(cuenta.getSaldo());
        dto.setTipoCuenta(cuenta.getTipoCuenta());
        dto.setEstado(cuenta.getEstado());
        dto.setNombreCliente(cuenta.getCliente() !=null ? cuenta.getCliente().getNombre() : "Sin cliente");
        return dto;
    }

    //DTO a Entidad (Datos que vienen del frontend)
    public CuentaBancaria toEntity(CuentaBancariaDTO dto){
        if (dto == null) return null;

        CuentaBancaria cuenta = new CuentaBancaria();
        Cliente cliente = new Cliente();

        cliente.setNombre(dto.getNombreCliente());
        cliente.setEdad(dto.getEdad());
        cliente.setDireccion(dto.getDireccion());
        cuenta.setCliente(cliente);
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setSaldo(dto.getSaldo());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setEstado(dto.getEstado());

        return  cuenta;
    }
}
