-- Script para actualizar usuarios existentes con DNIs únicos
-- Ejecutar este script después de agregar la columna dni

-- Actualizar usuarios existentes que no tienen DNI
-- Este script asigna DNIs temporales únicos a usuarios existentes
-- Deberás actualizar manualmente con los DNIs reales de cada usuario

-- Ejemplo para actualizar usuarios existentes:
-- UPDATE Usuario SET dni = CONCAT('TEMP_', id) WHERE dni IS NULL;

-- Después de actualizar todos los DNIs con valores reales, hacer la columna NOT NULL:
-- ALTER TABLE Usuario ALTER COLUMN dni SET NOT NULL;

-- Nota: Reemplaza 'TEMP_' con los DNIs reales de cada usuario antes de hacer la columna NOT NULL 