-- Borra TODAS las publicaciones y filas relacionadas (ofertas, chats, mensajes, imágenes asociadas).
-- Ejecutalo en Supabase: SQL Editor → New query → Run.
-- No borra usuarios ni tarjetas.

BEGIN;

DELETE FROM mensaje;
DELETE FROM chat;
DELETE FROM oferta;
DELETE FROM publicacion_imagenes;
DELETE FROM publicacion;

COMMIT;
