# Sistema de Gestion de Cuentas Bancarias - THE BANK

### Descripcion del Proyecto
Solucion Backend desarrollada con Spring Boot para la gestion de servicios financieros. El sistema permite administrar cuentas bancarias, procesar transacciones de deposito y transferencia, y gestionar el estado operativo de los usuarios.

### Arquitectura y Tecnologias
* Backend: Java 121 / Spring Boot 4.1.0.
* Patron de Diseño: Arquitectura multicapa (Controller, Service, Repository) con implementacion de DTOs y Mappers.
* Base de Datos: H2 Database en modo archivo (file) para persistencia local.
* Seguridad: Validaciones de integridad mediante Bean Validation.
* Reportes: Generacion de estados de cuenta en formato PDF.

### Endpoints de la API
* Login: POST /api/cuentas/login.
* Listado General: GET /api/cuentas.
* Depositos: POST /api/cuentas/deposito.
* Transferencias: POST /api/cuentas/transferir.
* Gestion de Estado: PATCH /api/cuentas/{num}/estado.
* Exportacion PDF: GET /api/cuentas/exportar-pdf/{num}.

### Instrucciones para Ejecutar el Proyecto
1. Importar el proyecto como Maven Project en IntelliJ IDEA.
2. Asegurar la disponibilidad del puerto 8080.
3. Ejecutar la clase SistemaBancarioApplication.java.
4. Acceder a la consola H2 en: http://localhost:8080/h2-console.
5. Configurar el JDBC URL en la consola H2 como: jdbc:h2:file:./data/sistema_bancario.

### Frontend
Interfaz desarrollada en React.js que consume la API REST para la visualizacion de datos y ejecucion de formularios de registro y operaciones financieras.
   
