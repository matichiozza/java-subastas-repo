-- Agregar columna CBU a la tabla Usuario
ALTER TABLE Usuario ADD COLUMN cbu VARCHAR(255);

-- Agregar restriccion de unicidad
ALTER TABLE Usuario ADD CONSTRAINT uk_usuario_cbu UNIQUE (cbu); 