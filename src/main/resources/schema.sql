-- ============================================
-- DRINKHOUSE - ESQUEMA DE BASE DE DATOS H2
-- Se ejecuta automáticamente antes de data.sql
-- ============================================

-- Tabla de negocios
CREATE TABLE IF NOT EXISTS negocios (
    negocio_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ruc VARCHAR(20) UNIQUE NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT NOW()
);

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    rol_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255)
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    usuario_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    nombre_completo VARCHAR(200) NOT NULL,
    password_hash VARCHAR(255),
    estado_cuenta VARCHAR(20) DEFAULT 'ACTIVO',
    creado_en TIMESTAMP DEFAULT NOW(),
    negocio_id BIGINT,
    rol_id BIGINT,
    FOREIGN KEY (negocio_id) REFERENCES negocios(negocio_id),
    FOREIGN KEY (rol_id) REFERENCES roles(rol_id)
);

-- Tabla de categorías
CREATE TABLE IF NOT EXISTS categorias (
    categoria_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    margen_ganancia_pct DECIMAL(5,2) DEFAULT 0.0,
    activo BOOLEAN DEFAULT TRUE,
    negocio_id BIGINT,
    FOREIGN KEY (negocio_id) REFERENCES negocios(negocio_id)
);

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    proveedor_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ruc VARCHAR(20) UNIQUE NOT NULL,
    razon_social VARCHAR(200) NOT NULL,
    direccion VARCHAR(300),
    telefono VARCHAR(20),
    email VARCHAR(150),
    negocio_id BIGINT,
    FOREIGN KEY (negocio_id) REFERENCES negocios(negocio_id)
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    producto_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    codigo_barra VARCHAR(50) UNIQUE,
    precio_compra DECIMAL(10,2) DEFAULT 0.00,
    precio_venta DECIMAL(10,2) DEFAULT 0.00,
    stock_minimo INTEGER DEFAULT 0,
    stock_actual INTEGER DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT NOW(),
    categoria_id BIGINT,
    negocio_id BIGINT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(categoria_id),
    FOREIGN KEY (negocio_id) REFERENCES negocios(negocio_id)
);

-- Tabla de ordenes de compra
CREATE TABLE IF NOT EXISTS ordenes_compra (
    orden_compra_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_referencia VARCHAR(50) UNIQUE NOT NULL,
    estado VARCHAR(20) DEFAULT 'BORRADOR',
    total DECIMAL(12,2) DEFAULT 0.00,
    creado_en TIMESTAMP DEFAULT NOW(),
    proveedor_id BIGINT NOT NULL,
    negocio_id BIGINT,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(proveedor_id),
    FOREIGN KEY (negocio_id) REFERENCES negocios(negocio_id)
);

-- Tabla de detalle de ordenes de compra  
CREATE TABLE IF NOT EXISTS detalle_orden_compra (
    detalle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) GENERATED ALWAYS AS (cantidad * precio_unitario),
    orden_compra_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    FOREIGN KEY (orden_compra_id) REFERENCES ordenes_compra(orden_compra_id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(producto_id)
);