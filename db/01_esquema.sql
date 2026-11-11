-- Food Store | Trabajo Practico Integrador - Programacion II (UTN)
-- Instalacion para MySQL 8: base independiente del TFI de Bases de Datos I.
-- Basado en el modelo validado del dominio: categoria (1) -> (N) producto.
-- No elimina tablas ni datos, y no modifica el esquema de Bases de Datos I.
-- Diseñado para una instalacion nueva; no corrige tablas preexistentes distintas.

CREATE DATABASE IF NOT EXISTS foodstore_prog2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE foodstore_prog2;

-- Instalacion no destructiva: no elimina tablas ni datos.
-- Si las tablas ya existen, verificar su estructura con SHOW CREATE TABLE.
CREATE TABLE IF NOT EXISTS categoria (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nombre_activo VARCHAR(100)
        GENERATED ALWAYS AS (
            CASE WHEN eliminado = 0 THEN nombre ELSE NULL END
        ) STORED,

    CONSTRAINT pk_categoria PRIMARY KEY (id),
    CONSTRAINT uq_categoria_nombre_activo UNIQUE (nombre_activo),
    CONSTRAINT chk_categoria_nombre
        CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT chk_categoria_eliminado
        CHECK (eliminado IN (0, 1))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS producto (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(255) NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen VARCHAR(255) NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    categoria_id INT UNSIGNED NOT NULL,

    CONSTRAINT pk_producto PRIMARY KEY (id),
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT chk_producto_nombre
        CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT chk_producto_precio
        CHECK (precio >= 0),
    CONSTRAINT chk_producto_stock
        CHECK (stock >= 0),
    CONSTRAINT chk_producto_disponible
        CHECK (disponible IN (0, 1)),
    CONSTRAINT chk_producto_eliminado
        CHECK (eliminado IN (0, 1))
) ENGINE = InnoDB;

-- Verificación de definiciones (no altera los registros):
SHOW CREATE TABLE categoria;
SHOW CREATE TABLE producto;

-- Comprobacion: tablas creadas en foodstore_prog2, sin usuarios ni pedidos.
-- La FK comprueba existencia de la categoria, no su estado activo.
-- Esa regla y la prohibicion de baja con productos activos se verificaran en servicios/DAO.
