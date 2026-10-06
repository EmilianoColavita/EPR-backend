# Despliegue en VPS con Dokploy

Guía para levantar backend + frontend + MySQL en un VPS (Hostinger) con Dokploy.
Ejemplo de dominios: `epr.com.ar` (frontend) y `api.epr.com.ar` (backend). Reemplazalos por los reales.

## 1. Preparar el VPS y el dominio

1. Instalar Dokploy en el VPS (Ubuntu): `curl -sSL https://dokploy.com/install.sh | sh`
2. Entrar al panel (`http://IP-DEL-VPS:3000`), crear el usuario admin y **activar 2FA**.
3. En el DNS del dominio crear dos registros **A** apuntando a la IP del VPS:
   `epr.com.ar` y `api.epr.com.ar` (y `www` si lo vas a usar).
4. Firewall: dejar abiertos solo 22 (SSH), 80 y 443. Una vez que le asignes un dominio al panel de
   Dokploy (Settings → Server Domain), cerrá también el 3000.
5. SSH: entrar con clave pública y deshabilitar el login por contraseña.

## 2. Base de datos (MySQL)

1. En Dokploy: Project → **Create Service → Database → MySQL** (versión 8).
2. Definí nombre de base (ej `epr_db`), usuario y contraseña (largas y aleatorias).
3. **No** completes "External Port": la base tiene que quedar accesible solo por la red interna.
4. En la pestaña Environment del MySQL agregá `TZ=America/Argentina/Buenos_Aires`
   (la query nativa de turnos usa `NOW()` del servidor MySQL).
5. Anotá el **Internal Host** que muestra Dokploy: es el `DB_HOST` del backend.

## 3. Backend

1. Create Service → **Application** → conectar el repo de GitHub del backend, rama `main`.
2. Build Type: **Dockerfile** (path `./Dockerfile`).
3. Environment (todas obligatorias salvo que se indique):

   ```
   DB_HOST=<internal host del MySQL>
   DB_PORT=3306
   DB_NAME=epr_db
   DB_USER=<usuario MySQL>
   DB_PASSWORD=<password MySQL>
   JWT_SECRET=<salida de: openssl rand -base64 64>
   ADMIN_EMAIL=<email real del admin>
   ADMIN_PASSWORD=<contraseña fuerte, mínimo 8>
   FRONTEND_URL=https://epr.com.ar
   MAIL_USERNAME=<cuenta gmail>
   MAIL_PASSWORD=<contraseña de aplicación de Gmail>
   MAIL_FROM=<cuenta gmail o alias verificado>
   # opcionales
   # JWT_EXPIRATION_MS=604800000
   # CORS_ALLOWED_ORIGINS=https://epr.com.ar,https://www.epr.com.ar
   ```

   `SPRING_PROFILES_ACTIVE=prod` y la zona horaria ya vienen seteados en el Dockerfile.
   Si falta una variable obligatoria o el `JWT_SECRET` es corto o el de ejemplo, la app **no arranca**
   (mirá los logs del deploy).
4. Domains: `api.epr.com.ar` → container port **8080**, HTTPS activado (Let's Encrypt).
5. Advanced → Resources: asignale al menos **768 MB–1 GB** de memoria (la JVM usa el 75%).
6. Deploy. En los logs tiene que aparecer `Started BackendApplication`.

> Gmail: la "contraseña de aplicación" se genera en la cuenta de Google → Seguridad → Verificación en
> 2 pasos → Contraseñas de aplicaciones. Con la contraseña normal el envío falla.

## 4. Frontend

1. Create Service → Application → repo del frontend, Build Type **Dockerfile**.
2. `NEXT_PUBLIC_API_URL=https://api.epr.com.ar` tiene que ir como **Build Arg / build-time variable**
   (Next la incrusta al compilar; cargarla solo como variable de runtime no alcanza).
   Si la cambiás, hay que volver a deployar el front.
3. Domains: `epr.com.ar` → container port **3000**, HTTPS activado.

## 5. Backups (antes de cargar datos reales)

1. Crear un bucket S3-compatible fuera del VPS (Cloudflare R2, Backblaze B2 o AWS S3).
2. Dokploy → Settings → **Destinations** → agregar el bucket.
3. En el servicio MySQL → **Backups** → programar diario (ej `0 4 * * *`) hacia ese destino.
4. **Probar una restauración** una vez: un backup que nunca se restauró no está probado.

Los archivos (evaluaciones, rutinas PDF, comprobantes, fotos) se guardan dentro de la base, así que el
backup de MySQL incluye todo.

## 6. Verificación después del primer deploy

- [ ] `https://api.epr.com.ar/api/v1/planes` responde JSON.
- [ ] `https://api.epr.com.ar/swagger-ui.html` y `/api-docs` responden **404** (deshabilitados en prod).
- [ ] El front carga los planes en la home (si no, revisar `NEXT_PUBLIC_API_URL` y CORS/`FRONTEND_URL`).
- [ ] Login con el admin. Después, cambiale la contraseña con "Olvidé mi contraseña" y sacá
      `ADMIN_EMAIL` y `ADMIN_PASSWORD` de las variables (solo se exigen cuando la base está vacía).
- [ ] "Olvidé mi contraseña" envía el mail y el link apunta al dominio real.
- [ ] Subir un comprobante desde un alumno y descargarlo desde el admin.
- [ ] Probar 6 logins fallidos con el mismo email: el 6º tiene que devolver "Demasiados intentos".

## 7. Mantenimiento

- **Autodeploy**: en cada servicio activá el webhook de GitHub para que cada push a `main` redeploye.
  El backend hace cierre ordenado (termina los requests en curso antes de apagarse).
- **Esquema de base**: se usa `ddl-auto=update`, que agrega tablas y columnas pero nunca renombra ni
  borra. Si renombrás un campo de una entidad, la columna vieja queda y hay que migrar a mano.
  Antes de un cambio grande de entidades, hacé un backup manual.
- **Rate limiting**: los contadores están en memoria, así que se reinician con cada deploy y solo son
  válidos con **una** instancia del backend (no escalar a réplicas sin cambiar eso).
- **Logs**: los errores inesperados quedan en los logs del backend en Dokploy, con el stack trace
  completo (al cliente solo le llega un mensaje genérico).
