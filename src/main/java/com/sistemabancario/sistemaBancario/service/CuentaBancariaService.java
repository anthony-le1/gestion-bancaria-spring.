package com.sistemabancario.sistemaBancario.service;

import com.sistemabancario.sistemaBancario.dto.CuentaBancariaDTO;
import com.sistemabancario.sistemaBancario.exceptions.BancarioException;
import com.sistemabancario.sistemaBancario.exceptions.DatosInvalidosException;
import com.sistemabancario.sistemaBancario.exceptions.ResourceNotFoundException;
import com.sistemabancario.sistemaBancario.exceptions.SaldoInsuficienteException;
import com.sistemabancario.sistemaBancario.mapper.CuentaBancariaMapper;
import com.sistemabancario.sistemaBancario.model.Cliente;
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
    public CuentaBancariaDTO actualizarDatosPersonales(String numeroCuenta, CuentaBancariaDTO dto) {
        // 1. Buscamos la cuenta
        CuentaBancaria cuentaExistente = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        //Datos que el Admin puede corregir
        if (cuentaExistente.getCliente() != null) {
            Cliente c = cuentaExistente.getCliente();

            // CORRECCIONES PERMITIDAS
            c.setNombre(dto.getNombreCliente());
            c.setDireccion(dto.getDireccion());
            c.setTelefono(dto.getTelefono());
            c.setCorreo(dto.getCorreo());

        }

        CuentaBancaria cuentaActualizada = cuentaBancariaRepository.save(cuentaExistente);
        return cuentaBancariaMapper.toDTO(cuentaActualizada);
    }

    @Override
    public CuentaBancariaDTO autenticarCliente(String cedula, String password) {
        // Buscamos al cliente por cédula (debes tener este método en tu repo)
        return cuentaBancariaRepository.findByClienteCedula(cedula)
                .filter(cuenta -> cuenta.getPassword().equals(password)) // Verificamos clave
                .map(cuenta -> {
                    CuentaBancariaDTO dto = cuentaBancariaMapper.toDTO(cuenta);
                    dto.setRol("CLIENTE"); // Le asignamos el rol para el frontend
                    return dto;
                })
                .orElse(null); // Si no existe o la clave falla, devuelve null
    }


    @Override
    public CuentaBancariaDTO crearCuenta(CuentaBancariaDTO cuentaDTO){

        // Validar edad
        if (cuentaDTO.getEdad() < 18) {
            throw new DatosInvalidosException("El cliente debe ser mayor de edad.");
        }

        //Validacion Límite de 3 cuentas (2 ahorros, 1 corriente)
        List<CuentaBancaria> cuentasDelCliente = cuentaBancariaRepository.findByCliente_NombreIgnoreCase(cuentaDTO.getNombreCliente());

        long ahorros = cuentasDelCliente.stream().filter(c -> c.getTipoCuenta().equalsIgnoreCase("AHORROS")).count();
        long corriente = cuentasDelCliente.stream().filter(c -> c.getTipoCuenta().equalsIgnoreCase("CORRIENTE")).count();

        if (cuentaDTO.getTipoCuenta().equalsIgnoreCase("AHORROS") && ahorros >= 2) {
            throw new DatosInvalidosException("Límite alcanzado: Un cliente solo puede tener 2 cuentas de Ahorros.");
        }
        if (cuentaDTO.getTipoCuenta().equalsIgnoreCase("CORRIENTE") && corriente >= 1) {
            throw new DatosInvalidosException("Límite alcanzado: Un cliente solo puede tener 1 cuenta Corriente.");
        }

        //Generación de Número Único de cuenta
        String numeroGenerado;
        do {
            numeroGenerado = generarNumeroAleatorio();
        } while (cuentaBancariaRepository.existsByNumeroCuenta(numeroGenerado));

        //Asignamos el numero de cuenta
        cuentaDTO.setNumeroCuenta(numeroGenerado);


        //Mapeo, Guardado y Retorno
        CuentaBancaria entidad = cuentaBancariaMapper.toEntity(cuentaDTO);
        CuentaBancaria guardada = cuentaBancariaRepository.save(entidad);

        return cuentaBancariaMapper.toDTO(guardada);
    }

    //Generar cuenta aleatoria
    private String generarNumeroAleatorio() {
        String numero;
        boolean existe;
        do {
            // Genera los 10 dígitos
            numero = String.valueOf((long) (Math.random() * 9_000_000_000L) + 1_000_000_000L);
            // Pregunta al repositorio si ese número ya lo tiene alguien más
            existe = cuentaBancariaRepository.existsByNumeroCuenta(numero);
        } while (existe); // Si ya existe, vuelve a generar otro diferente

        return numero;
    }

    @Override
    public CuentaBancariaDTO consultarSaldo(String numeroCuenta){
        //Buscamos la entidad
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new ResourceNotFoundException("Cuenta" + numeroCuenta + " no existe."));
        return   cuentaBancariaMapper.toDTO(cuenta);
    }

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
