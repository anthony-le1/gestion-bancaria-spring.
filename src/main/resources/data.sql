-- Opción A: Insertar sin especificar columnas para evitar el error de nombre
INSERT INTO clientes VALUES (1, '9999999999', 'ADMINISTRADOR CENTRAL');

-- Y la cuenta (ajustando al ID 1 del cliente)
INSERT INTO cuentas (numero_cuenta, saldo, estado, password, cliente_id)
VALUES ('0000000000', 0.0, 'ACTIVA', 'admin123', 1);