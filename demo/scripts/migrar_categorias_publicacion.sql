-- Migración de categorías (enum STRING en columna categoria de publicacion).
-- Ajusta el CHECK de PostgreSQL al catálogo actual (Java enum Categoria).
-- 1) Excepción Joyería: Satsuma → VAJILLAS; resto Joyería → RELOJES.
-- 2) Categorías viejas que ya no existen → MISCELANEA.

BEGIN;

ALTER TABLE publicacion DROP CONSTRAINT IF EXISTS publicacion_categoria_check;

UPDATE publicacion
SET categoria = 'VAJILLAS'
WHERE categoria = 'JOYERIA'
  AND LOWER(titulo) LIKE '%satsuma%';

UPDATE publicacion
SET categoria = 'RELOJES'
WHERE categoria = 'JOYERIA';

UPDATE publicacion
SET categoria = 'MISCELANEA'
WHERE categoria IN (
  'HOGAR',
  'COCINA',
  'ACCESORIOS',
  'DEPORTES',
  'AIRE_LIBRE',
  'VEHICULOS',
  'JUGUETES',
  'BEBES',
  'MASCOTAS',
  'ARTE'
);

ALTER TABLE publicacion
  ADD CONSTRAINT publicacion_categoria_check CHECK (
    categoria IN (
      'AUTOGRAFOS',
      'CALZADO',
      'CARTELES',
      'CERAMICA',
      'COLECCIONABLES',
      'COMICS',
      'COMPUTACION',
      'DIBUJO',
      'DOCUMENTOS',
      'ELECTRONICA',
      'ESCULTURA',
      'FOTOGRAFIA',
      'GEMAS',
      'GRABADOS',
      'HERRAMIENTAS',
      'INSTRUMENTOS',
      'JOYERIA',
      'LIBROS',
      'MAPAS',
      'MISCELANEA',
      'MODA',
      'MUEBLES',
      'MUSICA',
      'NUMISMATICA',
      'PERFUMES',
      'PINTURA',
      'PORCELANA',
      'RADIOS',
      'REVISTAS',
      'RELOJES',
      'TELEFONOS',
      'VAJILLAS'
    )
  );

COMMIT;
