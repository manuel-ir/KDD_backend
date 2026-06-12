# KDD — Contexto de sesión

## 1. Descripción del proyecto

**KDD** es una app móvil Android de planes grupales (TFG de 2º DAM).
Permite crear planes, unirse a comunidades, chatear, valorar usuarios, etc.
Fecha límite: **16 de junio de 2026** (presentación, despliegue y documentación).

### Tech stack
- **Backend:** Spring Boot 4.0.6, Java 17, Maven, MySQL 8.4
- **Seguridad:** Spring Security + JWT (jjwt 0.12.3) + Firebase Admin SDK 9.2.0
- **Frontend:** Kotlin + Jetpack Compose, compileSdk 36, minSdk 26
- **Red:** Retrofit2 + OkHttp, DataStore Preferences
- **Control de versiones:** GitHub — dos repos separados:
  - https://github.com/manuel-ir/KDD_backend (antes llamado KDD)
  - https://github.com/manuel-ir/KDD_frontend

### Estructura de carpetas
```
C:\Users\aniki\IdeaProjects\KDD\
├── kdd_backend\                  ← proyecto Spring Boot (repo KDD_backend en GitHub)
├── KDD_frontend\                 ← proyecto Android (repo KDD_frontend en GitHub)
│   └── app\src\main\java\com\kdd\kdd_frontend\
│       ├── data\TokenDataStore.kt
│       ├── navigation\NavGraph.kt
│       ├── network\ApiClient.kt + ApiService.kt
│       │   └── dto\ (AuthRequest, AuthResponse, PlanDto, UsuarioDto, ComunidadDto,
│       │             CrearPlanDto, CrearComunidadDto, MiembroComunidadDto,
│       │             AmistadDto, MensajeDto)
│       ├── ui\screens\     (15 pantallas)
│       ├── ui\theme\
│       └── viewmodel\
│           ├── AuthViewModel.kt
│           ├── PlanViewModel.kt
│           ├── ComunidadViewModel.kt
│           └── PerfilViewModel.kt
└── CONTEXT.md
```

---

## 2. Estado actual del backend

### Todos los archivos están en disco (rama feature/chat-basico), SIN commitear aún

El backend completo está implementado pero no tiene commits desde el setup inicial.
Contiene todas las entidades, servicios, controladores y seguridad.

### Problema conocido del índice de git
Antes de cualquier commit en el repo raíz:
```bash
cd C:/Users/aniki/IdeaProjects/KDD && git reset HEAD
```

---

## 3. Endpoints implementados

```
GET  /api/health                              ← público
POST /api/auth/google                         ← público, recibe idToken Firebase
GET  /api/usuarios/me                         ← requiere JWT
PUT  /api/usuarios/me                         ← requiere JWT
GET  /api/planes                              ← requiere JWT
GET  /api/planes/mis-planes                   ← requiere JWT
GET  /api/planes/{id}                         ← requiere JWT
POST /api/planes                              ← requiere JWT
POST /api/planes/{id}/unirse                  ← requiere JWT
DELETE /api/planes/{id}/abandonar             ← requiere JWT
GET  /api/comunidades                         ← requiere JWT
GET  /api/comunidades/{id}                    ← requiere JWT
POST /api/comunidades                         ← requiere JWT
POST /api/comunidades/{id}/unirse             ← requiere JWT
DELETE /api/comunidades/{id}/abandonar        ← requiere JWT
GET  /api/comunidades/{id}/miembros           ← requiere JWT
POST /api/valoraciones                        ← requiere JWT
GET/POST/PUT/DELETE /api/amistades/{id}       ← requiere JWT
GET/POST /api/mensajes/{id}                   ← requiere JWT
```

---

## 4. Modelo de datos (ER completo)

10 entidades JPA en MySQL:
- **Usuario**: id, nombre, email, password, fotoPerfil, googleId, fechaRegistro, esInvitado, descripcion, **fechaNacimiento** (reemplaza edad — se calcula dinámicamente en UsuarioService.toDto)
- **Plan**: id, titulo, descripcion, fechaEvento, horaEvento, ubicacionTexto, iconos, fechaCreacion, edadMin, edadMax, numMaxPersonas, idioma, creador(FK), categoria(FK)
- **Categoria**: id, tipo
- **Comunidad**: id, nombre, descripcion, ubicacion, fechaCreacion, edadMax, edadMin, admin(FK)
- **Mensaje**: id, contenido, fechaEnvio, emisor(FK), receptor(FK)
- **Participacion** (N:M): usuarioId+planId, estado="confirmado" (directo, sin pendiente)
- **PertenenciaComunidad** (N:M): usuarioId+comunidadId, estado="confirmado" (directo)
- **PertenenciaPlanComunidad** (N:M): planId+comunidadId, fechaUnion, estado
- **Valoracion** (ternaria): idValorador+idValorado+idPlan, puntuacion (1-5), comentario
- **Amistad** (N:M reflexiva): usuarioId+amigoId, estado, fechaSolicitud, fechaConfirmacion

**PENDIENTE:** Añadir latitud y longitud al modelo Plan para Google Maps.

---

## 5. Estado del frontend

### Rama activa: `feature/connect-backend`
Todos los commits de conexión van a esta rama. PR a develop cuando esté todo conectado.

### Pantallas/flujos conectados al backend ✓:
- **LoginScreen** — Google Sign-In + Firebase + POST /api/auth/google
- **ExploreScreen** — GET /api/planes
- **PlanDetailScreen** — GET /api/planes/{id} + unirse/abandonar con botón según estado (creador / participante / externo)
- **CreatePlanScreen** — POST /api/planes
- **CommunitiesScreen** — GET /api/comunidades
- **CommunityDetailScreen** — GET /api/comunidades/{id} + unirse/abandonar + pestaña Miembros (foto, nombre, edad)
- **CreateCommunityScreen** — POST /api/comunidades (con SnackBar de error)
- **AccountScreen** — GET /api/usuarios/me + logout
- **EditProfileScreen** — PUT /api/usuarios/me con DatePicker para fechaNacimiento
- **CalendarScreen** — GET /api/planes/mis-planes

### Pantallas pendientes de conectar:
- ChatsScreen / ChatDetailScreen → GET/POST /api/mensajes/{id}
- FriendsScreen → GET/POST/PUT/DELETE /api/amistades

### Lógica de unirse/abandonar:
- Tanto planes como comunidades: el admin/creador ve "Eres el admin / Tu plan", miembro ve "Abandonar" (rojo), externo ve "Unirse" (morado)
- El estado (miembro/creador/admin) lo devuelve el backend en el DTO al hacer GET del detalle
- Al unirse, estado directo = "confirmado" (sin flujo de aprobación)

### DTOs frontend (Kotlin):
- `PlanDto`: id, titulo, descripcion, categoria, fechaEvento, horaEvento, ubicacionTexto, edadMin, edadMax, numMaxPersonas, idioma, anfitrionNombre, anfitrionId, numParticipantes, **miembro**, **creador**
- `ComunidadDto`: id, nombre, descripcion, ubicacion, edadMin, edadMax, adminNombre, adminId, numMiembros, **miembro**, **admin**
- `UsuarioDto`: id, nombre, email, fotoPerfil, descripcion, **edad** (calculada), **fechaNacimiento** (ISO)
- `MiembroComunidadDto`: id, nombre, fotoPerfil, edad

### Truco Retrofit — wildcard:
`Map<String, Any>` en Kotlin → error en Retrofit. Solución:
- DTOs tipados para endpoints complejos (CrearComunidadDto, CrearPlanDto)
- `Map<String, @JvmSuppressWildcards Any>` para maps pequeños (editarPerfil, valorar)

### Ramas en KDD_frontend:
| Rama | Estado |
|------|--------|
| develop | Base con setup-frontend mergeado |
| feature/connect-backend | **Rama activa** — toda la conexión backend |
| feature/categorias-valoraciones-ui | NO se mergea hasta que toque valoraciones |

---

## 6. Credenciales y configuración

### MySQL
- Usuario: root / Contraseña: 1234
- BD: kdd (createDatabaseIfNotExist=true)

### Variables de entorno (application.yaml con fallback local)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_API_KEY`

### JWT
- Secret local: `KDD-SecretKey-Para-Token-JWT-2024-MuyLargoYSeguro-Minimo256BitsParaHS256`
- Expiración: 86400000 ms (24h)

### Firebase / Google
- Web Client ID: `1045671394809-32or29g8gilr4gs2rm29j2o58u9mkujh.apps.googleusercontent.com`
- `firebase-service-account.json` en `kdd_backend/src/main/resources/` — NO subir a git
- `google-services.json` en `KDD_frontend/app/` — NO subir a git (en .gitignore)

### Emulador Android
- IP backend: `10.0.2.2:8080`

### Despliegue
- Plataforma: **Render** (recomendado por el profesor)
- Requiere cambio de MySQL → PostgreSQL antes del despliegue

---

## 7. Flujo de autenticación

1. Android → Google Sign-In → token de Google
2. Android → Firebase signInWithCredential → Firebase ID token (aud = project-kdd-cc9c0)
3. Android → POST /api/auth/google { idToken: Firebase ID token }
4. Backend verifica con Firebase Admin SDK
5. Busca o crea usuario en MySQL por googleId
6. Genera JWT propio (HMAC-SHA256, válido 24h)
7. Android guarda JWT en DataStore + ApiClient.jwtToken en memoria
8. Todas las peticiones incluyen `Authorization: Bearer <jwt>`

**IMPORTANTE:** El token que se envía al backend es el Firebase ID token (aud = project-kdd-cc9c0), NO el Google OAuth token (aud = client_id).

---

## 8. Próximos pasos (en orden)

1. **Chats** — ChatsScreen + ChatDetailScreen → GET/POST /api/mensajes/{id}
2. **Amistades** — FriendsScreen → /api/amistades
3. **Valoraciones** — conectar estrellas en PlanDetailScreen a POST /api/valoraciones
4. **Planes dentro de comunidades** — crear plan vinculado a comunidad + mostrarlo en CommunityDetailScreen pestaña Actividades
5. **Google Maps** — SDK + marcadores + latitud/longitud en Plan
6. **Cambio MySQL → PostgreSQL** para Render
7. **Despliegue en Render**
8. **Documentación** (secciones 8, 9, 11, 12 del Hito 1)
9. **Presentación + polish**

---

## 9. Reglas del proyecto

- Sin evidencias de IA: sin comentarios en chino, sin separadores estéticos
- Commits en español, naturales
- Nivel de código: estudiante de 2º DAM
- Sin FetchType.LAZY, sin comentarios en cada método
- Commits con fecha real (sin GIT_AUTHOR_DATE personalizado a partir de ahora)
- NOTAS_INTERNAS.md, build_docs.py y hito1_actualizado.docx en .gitignore
- Commits frontend desde: `C:/Users/aniki/IdeaProjects/KDD/KDD_frontend`
- Commits backend desde: `C:/Users/aniki/IdeaProjects/KDD`

---

## 10. Planificación (actualizada al 12 junio)

| Días | Tarea | Estado |
|------|-------|--------|
| 3-10 junio | Conexión frontend-backend (pantallas principales) | ✅ Hecho |
| 11-12 junio | Miembros comunidad, fechaNacimiento, unirse/abandonar | ✅ Hecho |
| 12-13 junio | Chats + amistades + valoraciones |  |
| 13-14 junio | Google Maps + planes en comunidades |  |
| 14 junio | MySQL → PostgreSQL + despliegue Render |  |
| 14-15 junio | Documentación (secciones 8, 9, 11, 12) |  |
| 15-16 junio | Polish UI + presentación + ensayo |  |
| 16 junio | Entrega — merge develop → main |  |

---

## 11. Documentación (Hito 1)

Archivo: `C:\Users\aniki\IdeaProjects\KDD\hito1_actualizado.docx`
- 14 imágenes insertadas manualmente
- Secciones pendientes: 8 (Implementación), 9 (Pruebas), 11 (Documentación de Usuario), 12 (Conclusiones)
- Para editar: subir el .docx — editar XML con unpack/edit/pack — NO regenerar desde cero
