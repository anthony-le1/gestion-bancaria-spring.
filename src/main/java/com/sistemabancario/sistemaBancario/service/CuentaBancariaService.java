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
        CuentaBancaria cuentaExistente = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        if (cuentaExistente.getCliente() != null) {
            Cliente c = cuentaExistente.getCliente();
            if (dto.getDireccion() != null) {
                c.setDireccion(dto.getDireccion());
            }
            c.setNombre(dto.getNombreCliente());
            c.setTelefono(dto.getTelefono());
            c.setCorreo(dto.getCorreo());
        }

        CuentaBancaria cuentaActualizada = cuentaBancariaRepository.save(cuentaExistente);
        return cuentaBancariaMapper.toDTO(cuentaActualizada);
    }

    @Override
    public CuentaBancariaDTO autenticarCliente(String cedula, String password) {
        // Buscamos al cliente por cédula
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
        // Validación de Cédula
        if (cuentaDTO.getCedula() == null || cuentaDTO.getCedula().length() != 10) {
            throw new DatosInvalidosException("La cédula debe tener exactamente 10 dígitos.");
        }
        // Validación de Teléfono
        if (cuentaDTO.getTelefono() != null && cuentaDTO.getTelefono().length() > 10) {
            throw new DatosInvalidosException("El teléfono no puede exceder los 10 dígitos.");
        }

        // Validación de Contraseña
        if (cuentaDTO.getPassword() == null || cuentaDTO.getPassword().length() < 8) {
            throw new DatosInvalidosException("La contraseña debe tener al menos 8 caracteres.");
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
    @Override
    public CuentaBancariaDTO buscarPorId(Long id) {
        // Buscamos la entidad en el repositorio por su ID
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con ID: " + id));

        return cuentaBancariaMapper.toDTO(cuenta);
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
        CuentaBancaria cuenta = cuentaBancariaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta " + numeroCuenta + " no existe."));

        CuentaBancariaDTO dto = cuentaBancariaMapper.toDTO(cuenta);

        if (cuenta.getCliente() != null) {
            dto.setDireccion(cuenta.getCliente().getDireccion());
            dto.setCorreo(cuenta.getCliente().getCorreo());
        }

        return dto;
    }

    @Override
    public List<CuentaBancariaDTO> listarTodas(){
        List<CuentaBancaria> cuentas = cuentaBancariaRepository.findAll();
        return cuentas.stream().map(cuenta -> {
            CuentaBancariaDTO dto = cuentaBancariaMapper.toDTO(cuenta);

            if(cuenta.getCliente() != null) {
                dto.setDireccion(cuenta.getCliente().getDireccion());
                dto.setCorreo(cuenta.getCliente().getCorreo());
                dto.setCedula(cuenta.getCliente().getCedula());
            }
            return dto;
        }).toList();
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
