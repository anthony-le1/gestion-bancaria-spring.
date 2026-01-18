Sistema de gestion de Cuentas Bancaraias - API REST 
Descripcion del Proyecto 
Este sistema es una solución Backend desarrollada con spring boot para gestionar la información financiera de un banco.
**Arquitectura**: Modelo en capas Controller -> Service -> Repository
**Base de Datos:** H2 Database
**Validaciones:** Bean Validation

Atributos de la Entidad (Cuenta Bancaria)
**ID:** Autogenerado (Long).
**Número de Cuenta:** Cadena de 10 dígitos (@NotBlank, @Size).
**Titular:** Nombre del dueño de la cuenta (@NotBlank).
**Saldo:** Valor numérico (@NotNull, @Min).
**Tipo de Cuenta:** Ahorros o Corriente (@NotBlank).
**Fecha de Apertura:** Fecha de registro (LocalDate).

Frontend (Interfaz de Usuario)
La interfaz ha sido desarrollada con **React**, enfocándose en la experiencia de usuario y el diseño responsivo.

**Tecnología:** React.js 
**Comunicación:** Consumo de API REST mediante 
**Componentes:**
    * Tabla de visualización de cuentas.
    * Formulario de registro con validaciones en tiempo real.
   
