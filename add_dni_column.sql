-- Script para agregar la columna DNI a la tabla Usuario
-- Ejecutar este script en la base de datos existente

-- Agregar la columna DNI como nullable inicialmente
ALTER TABLE Usuario ADD COLUMN dni VARCHAR(255);

-- Agregar restricción de unicidad
ALTER TABLE Usuario ADD CONSTRAINT uk_usuario_dni UNIQUE (dni);

-- Nota: Si ya existen usuarios en la tabla, necesitarás:
-- 1. Actualizar manualmente los DNIs existentes con valores únicos
-- 2. Luego hacer la columna NOT NULL con: ALTER TABLE Usuario ALTER COLUMN dni SET NOT NULL; 