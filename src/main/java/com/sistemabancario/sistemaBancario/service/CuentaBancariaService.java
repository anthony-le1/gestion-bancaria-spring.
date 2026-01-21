package com.sistemabancario.sistemaBancario.service;

import com.sistemabancario.sistemaBancario.dto.CuentaBancariaDTO;
import com.sistemabancario.sistemaBancario.exceptions.BancarioException;
import com.sistemabancario.sistemaBancario.exceptions.DatosInvalidosException;
import com.sistemabancario.sistemaBancario.exceptions.ResourceNotFoundException;
import com.sistemabancario.sistemaBancario.exceptions.SaldoInsuficienteException;
import com.sistemabancario.sistemaBancario.mapper.CuentaBancariaMapper;
import com.sistemabancario.sistemaBancario.model.CuentaBancaria;
import com.sistemabancario.sistemaBancario.repository.ICuentaBancariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaBancariaService implements ICuentaBancariaService{
    private final ICuentaBancariaRepository cuentaBancariaRepository;
    private final CuentaBancariaMapper cuentaBancariaMapper; //Inyeccion que "traduce"

    @Override
// Retirar dinero (Excepción personalizada)
    public void retirar(String numeroCuenta, BigDecimal monto) {
        // Validar que el monto sea positivo
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosInvalidosException("El monto a retirar debe ser mayor a cero");
        }

        //Buscar la cuenta
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));

        //Verificar si la cuenta está activa
        if (!cuenta.getEstado().equalsIgnoreCase("ACTIVA")) {
            throw new BancarioException("No se puede retirar: La cuenta no está activa");
        }

        //Verificar saldo suficiente
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException("No tienes fondos suficientes. Saldo disponible: " + cuenta.getSaldo());
        }

        //Realizar la operación y guardar
        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaBancariaRepository.save(cuenta);
    }


    @Override
    //Depositar dinero
    public void depositar(String numeroCuenta, BigDecimal monto){

        //Validar monto positivo
        if(monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosInvalidosException("El monto a depositar debe ser mayor a cero");
        }

        //Buscamos la cuenta, si no existe lanzamos una excepcion personalizada
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        //Validar estado de la cuenta
        if(!cuenta.getEstado().equalsIgnoreCase("ACTIVA")){
            throw new BancarioException("No se puede despositar en la cuenta. " + cuenta.getEstado());
        }

        //Sumamos el monto al saldo actual
        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        //Guardamos los cambios en la base de datos
        cuentaBancariaRepository.save(cuenta);
    }

    @Transactional
    @Override
    public  void transferir(String cuentaOrigen, String cuentaDestino, BigDecimal monto){
        //Validaciones inicales
        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new DatosInvalidosException("La cuenta origen no puede ser igual a la cuenta destino.");
        }
        if(monto  == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosInvalidosException("El monto a transferir debe ser mayor a cero");
        }

        //Ejecutar el retiro de la cuenta origen, aqui se reutiliza el metodo retirar
        this.retirar(cuentaOrigen, monto);
        //Ejecutar el deposito de la cuenta destino
        this.depositar(cuentaDestino, monto);
    }

    @Override
    public CuentaBancariaDTO crearCuenta(CuentaBancariaDTO cuentaDTO){

        //Validar edad
        if (cuentaDTO.getEdad() < 18) {
            throw new DatosInvalidosException("El cliente debe ser mayor de edad.");
        }

        //Generación de Número de Cuenta Aleatorio
        if (cuentaDTO.getNumeroCuenta() == null || cuentaDTO.getNumeroCuenta().isEmpty()) {
            cuentaDTO.setNumeroCuenta(generarNumeroAleatorio());
        }

        //Validaciones - Cuenta existente
        if (cuentaBancariaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new ResourceNotFoundException("Error: El número de cuenta " + cuentaDTO.getNumeroCuenta() + " ya está asignado.");
        }
        boolean yaTieneEseTipo = cuentaBancariaRepository.findByNombreClienteIgnoreCase(cuentaDTO.getNombreCliente())
                .stream()
                .anyMatch(c -> c.getTipoCuenta().equals(cuentaDTO.getTipoCuenta()));

        if (yaTieneEseTipo) {
            throw new ResourceNotFoundException("El cliente " + cuentaDTO.getNombreCliente() +
                    " ya posee una cuenta de tipo " + cuentaDTO.getTipoCuenta() + ". Solo puede abrir una de un tipo diferente.");
        }
        //Convertimos el DTO que viene del frontend a entidad para que JPA lo entienda
        CuentaBancaria entidad = cuentaBancariaMapper.toEntity(cuentaDTO);
        //Aqui se guarda en la base de datos
        CuentaBancaria guardada = cuentaBancariaRepository.save(entidad);
        //Convertimos la entidad guardada de vuelta al DTO para responderle al FrontEnd
        return  cuentaBancariaMapper.toDTO(guardada);
    }

    //Generar cuenta aleatoria
    private String generarNumeroAleatorio() {
        return String.valueOf((long) (Math.random() * 9_000_000_000L) + 1_000_000_000L);
    }

    @Override
    public CuentaBancariaDTO consultarSaldo(String numeroCuenta){
        //Buscamos la entidad
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new ResourceNotFoundException("Cuenta" + numeroCuenta + " no existe."));
        return   cuentaBancariaMapper.toDTO(cuenta);
    }{}

    @Override
    public List<CuentaBancariaDTO> listarTodas(){
        //Aqui buscamos todas las entidades en la base de datos
        List<CuentaBancaria> cuentas = cuentaBancariaRepository.findAll();

        //Convertimos la lista de entidades a lista de DTO usando el mapper
        return cuentas.stream().map(cuentaBancariaMapper::toDTO).toList();
    }

    @Override
    public void cambiarEstado(String numeroCuenta, String nuevoEstado) {
        //Buscamos la cuenta
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new ResourceNotFoundException("Cuenta" + numeroCuenta + " no existe."));

        //Validamos que el estado sea valido
        if(!nuevoEstado.equalsIgnoreCase("ACTIVA") && !nuevoEstado.equalsIgnoreCase("INACTIVA")){
            throw new DatosInvalidosException("La cuenta debe ser ACTIVA o INACTIVA");
        }

        //Actualizamos y guardamos
        cuenta.setEstado(nuevoEstado.toUpperCase());
        cuentaBancariaRepository.save(cuenta);
    }

    @Override
    public void eliminarCuenta(String numeroCuenta) {
        //Verificamos si la cuenta existe antes de borrarla
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Cuenta no encontrada"));

        cuentaBancariaRepository.delete(cuenta);
    }

}
