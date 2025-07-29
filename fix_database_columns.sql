-- Script para actualizar las columnas de la tabla publicacion
-- Ejecutar este script en SQL Server Management Studio o en la consola de SQL Server

USE Subastas;

-- Actualizar la columna DESCRIPCION para permitir hasta 1000 caracteres
ALTER TABLE publicacion ALTER COLUMN DESCRIPCION NVARCHAR(1000);

-- Actualizar la columna TITULO para permitir hasta 200 caracteres
ALTER TABLE publicacion ALTER COLUMN TITULO NVARCHAR(200);

-- Actualizar la columna ESTADO para permitir hasta 50 caracteres
ALTER TABLE publicacion ALTER COLUMN ESTADO NVARCHAR(50);

-- Actualizar la columna CONDICION para permitir hasta 100 caracteres
ALTER TABLE publicacion ALTER COLUMN CONDICION NVARCHAR(100);

-- Actualizar la columna COMENTARIO_CANCELACION para permitir hasta 500 caracteres
ALTER TABLE publicacion ALTER COLUMN COMENTARIO_CANCELACION NVARCHAR(500);

-- Verificar los cambios
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'publicacion' 
AND COLUMN_NAME IN ('DESCRIPCION', 'TITULO', 'ESTADO', 'CONDICION', 'COMENTARIO_CANCELACION'); 