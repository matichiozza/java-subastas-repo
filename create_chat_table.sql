-- Crear tabla Chat
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Chat]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[Chat] (
        [ID] INT PRIMARY KEY IDENTITY(1,1),
        [PUBLICACION_ID] INT NOT NULL,
        [VENDEDOR_ID] INT NOT NULL,
        [GANADOR_ID] INT NOT NULL,
        [FECHA_CREACION] DATETIME NOT NULL,
        [ESTADO] NVARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
        CONSTRAINT FK_Chat_Publicacion FOREIGN KEY ([PUBLICACION_ID]) REFERENCES [dbo].[Publicacion]([ID]),
        CONSTRAINT FK_Chat_Vendedor FOREIGN KEY ([VENDEDOR_ID]) REFERENCES [dbo].[Usuario]([ID]),
        CONSTRAINT FK_Chat_Ganador FOREIGN KEY ([GANADOR_ID]) REFERENCES [dbo].[Usuario]([ID])
    );
    PRINT 'Tabla Chat creada exitosamente';
END
ELSE
BEGIN
    PRINT 'La tabla Chat ya existe';
END

-- Crear tabla Mensaje si no existe
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Mensaje]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[Mensaje] (
        [ID] INT PRIMARY KEY IDENTITY(1,1),
        [CHAT_ID] INT NOT NULL,
        [EMISOR_ID] INT NOT NULL,
        [CONTENIDO] NVARCHAR(1000) NOT NULL,
        [FECHA_ENVIO] DATETIME NOT NULL,
        [LEIDO] BIT NOT NULL DEFAULT 0,
        CONSTRAINT FK_Mensaje_Chat FOREIGN KEY ([CHAT_ID]) REFERENCES [dbo].[Chat]([ID]),
        CONSTRAINT FK_Mensaje_Emisor FOREIGN KEY ([EMISOR_ID]) REFERENCES [dbo].[Usuario]([ID])
    );
    PRINT 'Tabla Mensaje creada exitosamente';
END
ELSE
BEGIN
    PRINT 'La tabla Mensaje ya existe';
END

