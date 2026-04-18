/**
 * Ejecuta migrar_categorias_publicacion.sql usando credenciales de application.properties.
 * Uso (desde carpeta demo): npx --yes -p pg node scripts/run-migrate-categorias.mjs
 */
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import pg from 'pg';

const { Client } = pg;

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const demoRoot = path.join(__dirname, '..');
const propsPath = path.join(demoRoot, 'src', 'main', 'resources', 'application.properties');
const sqlPath = path.join(__dirname, 'migrar_categorias_publicacion.sql');

function getProp(text, key) {
  const m = text.match(new RegExp(`^${key}=(.+)$`, 'm'));
  return m ? m[1].trim() : null;
}

const props = fs.readFileSync(propsPath, 'utf8');
const jdbc = getProp(props, 'spring.datasource.url');
const user = getProp(props, 'spring.datasource.username');
const password = getProp(props, 'spring.datasource.password');

if (!jdbc || !user || password == null) {
  console.error('Faltan url/usuario/password en application.properties');
  process.exit(1);
}

const u = new URL(jdbc.replace(/^jdbc:/, ''));
const host = u.hostname;
const port = parseInt(u.port || '5432', 10);
const database = u.pathname.replace(/^\//, '') || 'postgres';

const sql = fs.readFileSync(sqlPath, 'utf8');

const client = new Client({
  host,
  port,
  user,
  password,
  database,
  ssl: { rejectUnauthorized: false },
});

await client.connect();
try {
  await client.query(sql);
  console.log('Migración aplicada:', sqlPath);
} finally {
  await client.end();
}
