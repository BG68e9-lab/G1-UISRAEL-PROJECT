-- ============================================
-- DRINKHOUSE - DATOS INICIALES PARA H2
-- Se ejecuta automáticamente al iniciar la aplicación
-- ============================================

-- 1. CREAR NEGOCIO PRINCIPAL
INSERT INTO negocios (nombre, ruc, activo, creado_en) 
VALUES ('DrinkHouse Store', '1234567890001', true, NOW());

-- 2. CREAR ROLES BÁSICOS
INSERT INTO roles (nombre, descripcion) 
VALUES 
    ('ADMIN', 'Administrador del sistema con acceso completo'),
    ('EMPLEADO', 'Empleado con acceso limitado a operaciones');

-- 3. CREAR USUARIO ADMINISTRADOR
-- Contraseña: admin123 (sin hash para prueba inicial)
INSERT INTO usuarios (email, nombre_completo, password_hash, estado_cuenta, creado_en, negocio_id, rol_id)
VALUES (
    'admin@drinkhouse.com',
    'Administrador Sistema',
    'admin123',
    'ACTIVO',
    NOW(),
    1,
    1
);

-- 4. CREAR CATEGORÍAS BÁSICAS
INSERT INTO categorias (nombre, margen_ganancia_pct, activo, negocio_id)
VALUES 
    ('Bebidas Alcohólicas', 35.0, true, 1),
    ('Bebidas No Alcohólicas', 25.0, true, 1),  
    ('Snacks', 40.0, true, 1),
    ('Cigarrillos', 15.0, true, 1),
    ('Dulces', 50.0, true, 1);

-- 5. CREAR PROVEEDOR DE EJEMPLO
INSERT INTO proveedores (proveedor_id, ruc, razon_social, direccion, telefono, email, negocio_id)
VALUES (
    1,
    '1234567890123',
    'Distribuidora La Favorita C.A.',
    'Av. Principal #123, Quito',
    '+593987654321',
    'ventas@lafavorita.com',
    1
);

-- 6. CREAR PRODUCTOS DE EJEMPLO
INSERT INTO productos (producto_id, nombre, codigo_barra, precio_compra, precio_venta, stock_minimo, stock_actual, activo, categoria_id, negocio_id)
VALUES 
    (1, 'Coca-Cola 600ml', '7702103002011', 0.85, 1.25, 10, 50, true, 2, 1),
    (2, 'Pepsi 600ml', '7702103002012', 0.80, 1.20, 10, 30, true, 2, 1),
    (3, 'Cerveza Pilsener 330ml', '7702103003011', 1.20, 1.75, 20, 100, true, 1, 1),
    (4, 'Doritos Nacho 150g', '7702103004011', 2.50, 3.50, 5, 25, true, 3, 1),
    (5, 'Marlboro Box', '7702103005011', 3.20, 3.68, 10, 40, true, 4, 1),
    (6, 'Chicles Trident', '7702103006011', 0.30, 0.60, 20, 80, true, 5, 1);