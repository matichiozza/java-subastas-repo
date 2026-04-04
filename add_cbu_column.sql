-- Agregar columna CBU a la tabla Usuario (sintaxis correcta para SQL Server)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Usuario]') AND name = 'cbu')
BEGIN
    ALTER TABLE [dbo].[Usuario] ADD [cbu] NVARCHAR(255);
    PRINT 'Columna cbu agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La columna cbu ya existe';
END

-- Agregar restriccion de unicidad
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'uk_usuario_cbu' AND object_id = OBJECT_ID(N'[dbo].[Usuario]'))
BEGIN
    ALTER TABLE [dbo].[Usuario] ADD CONSTRAINT uk_usuario_cbu UNIQUE ([cbu]);
    PRINT 'Restricción de unicidad para cbu agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La restricción uk_usuario_cbu ya existe';
END 