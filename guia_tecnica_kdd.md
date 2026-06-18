# Guía Técnica Completa — Proyecto KDD
> Documento de estudio: cómo funciona el proyecto de principio a fin

---

## 0. Visión general antes de entrar en detalle

KDD es una aplicación Android que permite a usuarios crear y unirse a planes grupales, organizarse en comunidades y chatear entre sí.

El proyecto tiene **tres capas** que se comunican entre sí:

```
┌─────────────────────┐
│  Android (Kotlin)   │  ← El usuario ve esto. App en el móvil.
│  KDD_frontend/      │
└────────┬────────────┘
         │ HTTPS (JSON)
         ▼
┌─────────────────────┐
│  Spring Boot (Java) │  ← El cerebro. Recibe peticiones, aplica lógica, devuelve datos.
│  kdd_backend/       │  ← Desplegado en Render (internet)
└────────┬────────────┘
         │ SQL
         ▼
┌─────────────────────┐
│  MySQL              │  ← La memoria. Guarda todos los datos permanentemente.
│  Base de datos kdd  │
└─────────────────────┘
```

Además hay un **servicio externo**: **Firebase** (de Google), que se usa solo para verificar que los usuarios de Google son quienes dicen ser.

---

## 1. Tecnologías usadas y por qué

### Frontend — Kotlin + Jetpack Compose

**Kotlin** es el lenguaje oficial de Android desde 2017. Es más moderno y seguro que Java: elimina los NullPointerException más comunes, tiene sintaxis más limpia y soporta corrutinas (forma de hacer peticiones de red sin bloquear la app).

**Jetpack Compose** es el sistema de interfaces declarativo de Google (desde 2021). En lugar de diseñar pantallas en XML y luego enlazarlas con código (el sistema antiguo con Activities y Fragments), en Compose describes cómo debe verse la pantalla **en función del estado**. Cuando el estado cambia, la pantalla se redibuja sola.

Ejemplo conceptual:
```kotlin
// Si isLoading es true → muestra spinner. Si no → muestra el contenido.
if (isLoading) CircularProgressIndicator() else ListaDePlanes(planes)
```

**Por qué Compose:** es el estándar actual de Google, más productivo que XML, y encaja perfectamente con el patrón MVVM que usa el proyecto.

---

### Backend — Spring Boot (Java)

**Spring Boot** es un framework de Java que permite crear servidores web (APIs REST) con muy poca configuración. Un API REST es básicamente un servidor que escucha peticiones HTTP (como las de un navegador web) y responde con datos en formato JSON.

En este proyecto, Spring Boot:
- Recibe peticiones HTTP del móvil
- Comprueba si el usuario está autenticado (JWT)
- Aplica la lógica de negocio (¿puede este usuario unirse a este plan?)
- Habla con MySQL para leer o guardar datos
- Devuelve la respuesta en JSON

**Por qué Spring Boot:** es el framework más usado en empresas para APIs Java, tiene un ecosistema enorme, y permite configurar seguridad, base de datos y más con anotaciones sencillas.

---

### Base de datos — MySQL

**MySQL** es un sistema de base de datos relacional. Guarda los datos en tablas con filas y columnas, con relaciones entre ellas (un plan tiene un creador que es un usuario, etc.).

**Spring Data JPA** es la capa que conecta Spring Boot con MySQL: en lugar de escribir SQL a mano, defines clases Java (llamadas entidades) que se mapean automáticamente a tablas. **Hibernate** (que actúa bajo el capó) genera el SQL.

La configuración `ddl-auto: update` hace que Hibernate cree o modifique las tablas automáticamente al arrancar el servidor, comparando las entidades Java con el esquema real de MySQL.

---

### Firebase Authentication

**Firebase** es una plataforma de Google (BaaS: Backend as a Service). En este proyecto se usa solo la parte de **Authentication** para verificar el Google Sign-In.

¿Qué es Google Sign-In? Es el botón "Continuar con Google" que ves en muchas apps. En lugar de gestionar contraseñas propias, delegas la autenticación a Google.

**Por qué Firebase y no directamente la API de Google:** Firebase simplifica enormemente la verificación. Google proporciona un SDK (Firebase Admin SDK) que en una sola llamada (`verifyIdToken`) confirma si un token de Google es legítimo.

---

### Render (despliegue del backend)

**Render** es una plataforma en la nube (como Heroku) donde se despliega el servidor Spring Boot para que esté accesible desde internet. Sin Render, el backend solo funcionaría en tu ordenador local.

---

## 2. Estructura de carpetas

### Repositorio KDD (backend)

```
KDD/
├── kdd_backend/
│   ├── src/main/java/com/kdd/kdd_backend/
│   │   ├── KddBackendApplication.java     ← Punto de entrada. Arranca Spring Boot.
│   │   ├── config/
│   │   │   ├── SecurityConfig.java        ← Define qué rutas necesitan JWT y cuáles son públicas
│   │   │   ├── FirebaseConfig.java        ← Inicializa el SDK de Firebase al arrancar
│   │   │   ├── TriggerInitializer.java    ← Crea los triggers de MySQL al arrancar
│   │   │   ├── DataInitializer.java       ← Inserta datos de prueba iniciales
│   │   │   └── GlobalExceptionHandler.java ← Captura errores y devuelve respuestas JSON claras
│   │   ├── controller/                    ← Reciben las peticiones HTTP y devuelven respuestas
│   │   │   ├── AuthController.java
│   │   │   ├── PlanController.java
│   │   │   ├── UsuarioController.java
│   │   │   ├── ComunidadController.java
│   │   │   ├── MensajeController.java
│   │   │   ├── AmistadController.java
│   │   │   ├── ValoracionController.java
│   │   │   ├── CategoriaController.java
│   │   │   └── HealthController.java      ← Solo GET /api/health para comprobar que el server vive
│   │   ├── service/                       ← Lógica de negocio (qué se hace con los datos)
│   │   │   └── AuthService.java
│   │   ├── repository/                    ← Acceso a la base de datos (consultas SQL automáticas)
│   │   │   ├── UsuarioRepository.java
│   │   │   ├── PlanRepository.java
│   │   │   └── ... (uno por entidad)
│   │   ├── model/                         ← Clases Java que representan las tablas de MySQL
│   │   │   ├── Usuario.java → tabla usuarios
│   │   │   ├── Plan.java    → tabla planes
│   │   │   ├── Comunidad.java
│   │   │   ├── Mensaje.java
│   │   │   ├── Participacion.java         ← Relación N:M usuario ↔ plan
│   │   │   ├── Amistad.java
│   │   │   └── Valoracion.java            ← Relación ternaria usuario↔usuario↔plan
│   │   ├── dto/                           ← Objetos de transferencia de datos (lo que entra y sale por HTTP)
│   │   │   ├── GoogleAuthRequest.java     ← { "idToken": "..." }
│   │   │   ├── AuthResponse.java          ← { "token": "...", "userId": 1, ... }
│   │   │   └── ...
│   │   └── security/
│   │       ├── JwtService.java            ← Genera y valida tokens JWT
│   │       └── JwtAuthFilter.java         ← Intercepta CADA petición para verificar el JWT
│   └── src/main/resources/
│       ├── application.yaml               ← Configuración: BD, puerto, clave JWT, Client ID de Google
│       └── firebase-service-account.json  ← Credenciales Firebase (NUNCA a GitHub)
```

### Repositorio KDD_frontend

```
KDD_frontend/
└── app/src/main/java/com/kdd/kdd_frontend/
    ├── MainActivity.kt                    ← Actividad única. Carga el NavGraph.
    ├── KddApplication.kt                  ← Clase Application. Inicializa Firebase en Android.
    ├── navigation/
    │   ├── Screen.kt                      ← Define todas las rutas (strings de navegación)
    │   └── NavGraph.kt                    ← Conecta rutas con pantallas. Decide pantalla inicial.
    ├── network/
    │   ├── ApiClient.kt                   ← Configura Retrofit con la URL base y el interceptor JWT
    │   ├── ApiService.kt                  ← Lista de todos los endpoints (interfaz que Retrofit implementa)
    │   └── dto/                           ← Clases Kotlin que representan el JSON que llega/sale
    ├── data/
    │   └── TokenDataStore.kt              ← Guarda el JWT en disco (DataStore = SharedPreferences moderno)
    ├── viewmodel/                         ← MVVM: la lógica entre la pantalla y la red
    │   ├── AuthViewModel.kt
    │   ├── PlanViewModel.kt
    │   ├── ComunidadViewModel.kt
    │   ├── PerfilViewModel.kt
    │   └── ChatViewModel.kt
    └── ui/
        └── screens/
            ├── auth/                      ← LoginScreen, EmailLoginScreen, RegisterScreen
            ├── plan/                      ← PlanDetailScreen, CreatePlanScreen, EditPlanScreen
            ├── explore/                   ← ExploreScreen, FiltersScreen
            ├── calendar/                  ← CalendarScreen
            ├── communities/               ← CommunitiesScreen, CommunityDetailScreen, CreateCommunityScreen
            ├── chat/                      ← ChatsScreen, ChatDetailScreen
            ├── main/                      ← MainScreen (contenedor con bottom nav bar)
            └── profile/                   ← AccountScreen, EditProfileScreen
```

---

## 3. Cómo funciona la autenticación (Firebase + JWT)

Esta es la parte más importante. Hay **dos sistemas de autenticación** que trabajan juntos.

### 3.1. ¿Por qué dos sistemas?

- **Firebase** sabe si una cuenta de Google es real o no. Es el portero de la puerta de entrada.
- **JWT propio** es el carnet de identificación que el proyecto emite una vez que sabe que el usuario es legítimo.

Usar JWT propio en lugar de seguir usando Firebase para todo permite que el backend sea independiente de Firebase para todas las peticiones posteriores al login.

---

### 3.2. Flujo completo de Google Sign-In (paso a paso)

```
ANDROID                    FIREBASE (Google)          BACKEND (Spring Boot)      MYSQL
   │                            │                            │                     │
   │ 1. El usuario pulsa        │                            │                     │
   │    "Continuar con Google"  │                            │                     │
   │──────────────────────────> │                            │                     │
   │                            │ 2. Google muestra el       │                     │
   │                            │    selector de cuentas     │                     │
   │ <────────────────────────  │                            │                     │
   │ 3. Google devuelve un      │                            │                     │
   │    idToken (cadena larga)  │                            │                     │
   │                            │                            │                     │
   │ 4. POST /api/auth/google   │                            │                     │
   │    { idToken: "ey..." }    │                            │                     │
   │──────────────────────────────────────────────────────> │                     │
   │                            │                            │                     │
   │                            │ 5. FirebaseAuth            │                     │
   │                            │ .verifyIdToken(idToken)    │                     │
   │                            │ <─────────────────────── │                     │
   │                            │ 6. ✅ Token válido.        │                     │
   │                            │    Devuelve: uid, email,  │                     │
   │                            │    nombre, foto           │                     │
   │                            │ ───────────────────────> │                     │
   │                            │                            │                     │
   │                            │                            │ 7. SELECT * FROM    │
   │                            │                            │    usuarios WHERE   │
   │                            │                            │    google_id = uid  │
   │                            │                            │ ──────────────────> │
   │                            │                            │ <────────────────── │
   │                            │                            │ 8. Si no existe:    │
   │                            │                            │    INSERT nuevo     │
   │                            │                            │    usuario          │
   │                            │                            │ ──────────────────> │
   │                            │                            │                     │
   │                            │                            │ 9. Genera JWT       │
   │                            │                            │    propio con       │
   │                            │                            │    userId + email   │
   │ 10. Respuesta:             │                            │                     │
   │  { token, userId,          │                            │                     │
   │    displayName, email }    │                            │                     │
   │ <─────────────────────────────────────────────────── │                     │
   │                            │                            │                     │
   │ 11. Guarda el JWT          │                            │                     │
   │     en DataStore           │                            │                     │
```

---

### 3.3. ¿Qué es un JWT y qué contiene?

JWT son las siglas de **JSON Web Token**. Es una cadena de texto dividida en 3 partes separadas por puntos:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyQGdtYWlsLmNvbSJ9.ABC123firma
      CABECERA                        DATOS (payload)                    FIRMA
```

- **Cabecera:** algoritmo usado (HS256 = HMAC-SHA256)
- **Datos (payload):** lo que se guarda dentro del token. En este proyecto:
  - `sub`: el `userId` del usuario en MySQL (ej: "1")
  - `email`: correo del usuario
  - `iat`: fecha de emisión (issued at)
  - `exp`: fecha de expiración (24h después de la emisión)
- **Firma:** resultado de aplicar el algoritmo HS256 a la cabecera + datos usando la clave secreta. Si alguien modifica el token, la firma no coincide y el servidor lo rechaza.

**La clave secreta** (`KDD-SecretKey-Para-Token-JWT-2024-MuyLargoYSeguro...`) solo la conoce el servidor. Está en `application.yaml` y en Render como variable de entorno.

**El token no está cifrado** (Base64 decodificable), pero sí está **firmado**: el servidor puede verificar que nadie lo ha modificado. Por eso nunca guardes datos sensibles dentro del JWT.

---

### 3.4. Cómo se usa el JWT en cada petición posterior

Tras el login, el móvil guarda el JWT en **DataStore** (equivalente moderno de SharedPreferences, persiste aunque se cierre la app).

En `ApiClient.kt` hay un interceptor OkHttp que funciona automáticamente:

```kotlin
.addInterceptor { chain ->
    val request = chain.request().newBuilder().apply {
        jwtToken?.let { token ->
            addHeader("Authorization", "Bearer $token")  // ← añade esto a CADA petición
        }
    }.build()
    chain.proceed(request)
}
```

Así, cuando la app hace `GET /api/planes`, la petición lleva:
```
GET /api/planes HTTP/1.1
Host: kdd-backend.onrender.com
Authorization: Bearer eyJhbGciOi...  ← el JWT
```

En el servidor, el `JwtAuthFilter` intercepta **cada petición** antes de que llegue al controlador:

```
Petición entrante
       ↓
JwtAuthFilter.doFilterInternal()
       ↓
¿Hay cabecera "Authorization: Bearer ..."?
       ↓ Sí
JwtService.isTokenValid(jwt)
       ↓ Válido
Extraer userId del token
       ↓
Registrar userId en SecurityContextHolder
       ↓
La petición llega al Controller
       ↓
El Controller obtiene el userId con:
    Long userId = (Long) SecurityContextHolder.getContext()
                         .getAuthentication().getPrincipal();
```

Si no hay token o es inválido, la petición llega sin autenticar y Spring Security devuelve **403 Forbidden** automáticamente.

---

### 3.5. Flujo de registro/login con email (sin Google)

Este flujo es más sencillo porque no involucra Firebase:

1. El usuario rellena nombre, email y contraseña en `RegisterScreen`
2. Android envía `POST /api/auth/registro-email` con esos datos
3. El backend valida los requisitos (longitud, mayúscula, número...)
4. Hashea la contraseña con **BCrypt** (nunca se guarda en texto plano)
5. Crea el usuario en MySQL con `proveedor = "email"`
6. Genera y devuelve el JWT

Para el login posterior:
1. Android envía `POST /api/auth/login-email` con email y contraseña
2. El backend busca al usuario por email
3. Compara la contraseña con el hash BCrypt: `passwordEncoder.matches(dto.getPassword(), usuario.getPassword())`
4. Si coincide, genera y devuelve el JWT

**BCrypt** es un algoritmo de hash diseñado para contraseñas. Es lento a propósito (para dificultar ataques de fuerza bruta) y añade una "sal" aleatoria, de forma que dos contraseñas iguales producen hashes distintos.

---

## 4. Arquitectura del frontend: MVVM

El frontend sigue el patrón **MVVM (Model - View - ViewModel)**:

```
PANTALLA (Screen / View)
    │ observa StateFlow
    ↓
VIEWMODEL
    │ llama a ApiClient
    ↓
NETWORK (ApiClient + ApiService)
    │ HTTP
    ↓
BACKEND
```

### ¿Por qué MVVM?

Sin MVVM, la lógica de negocio estaría mezclada con el código de la pantalla. Con MVVM:
- La pantalla solo sabe cómo dibujar cosas y qué función llamar al pulsar un botón.
- El ViewModel hace las peticiones de red, gestiona los errores y expone el resultado.
- Si el usuario gira el teléfono, la pantalla se destruye y recrea, pero el ViewModel sobrevive → los datos no se pierden.

### ¿Cómo se comunican ViewModel y pantalla?

Con **StateFlow** (de Kotlin Coroutines). El ViewModel expone un estado observable:

```kotlin
// En PlanViewModel:
private val _planes = MutableStateFlow<List<PlanDto>>(emptyList())
val planes: StateFlow<List<PlanDto>> = _planes

fun cargarPlanes() {
    viewModelScope.launch {          // ← corrutina: no bloquea la UI
        val response = ApiClient.api.getPlanes()
        if (response.isSuccessful) {
            _planes.value = response.body() ?: emptyList()
        }
    }
}
```

```kotlin
// En ExploreScreen:
val planes by viewModel.planes.collectAsState()
// Cada vez que _planes cambia en el ViewModel, la pantalla se redibuja automáticamente
```

### Los 5 ViewModels

| ViewModel | Gestiona |
|---|---|
| `AuthViewModel` | Login/registro, cierre de sesión |
| `PlanViewModel` | Planes: listar, crear, editar, unirse, historial |
| `ComunidadViewModel` | Comunidades: listar, crear, unirse, miembros |
| `PerfilViewModel` | Perfil propio: leer y editar |
| `ChatViewModel` | Conversaciones y amistades |

---

## 5. Navegación en el frontend

El proyecto usa **arquitectura de actividad única**: solo hay una `MainActivity`. Dentro de ella, `NavGraph` gestiona toda la navegación con un `NavController`.

`Screen.kt` define las rutas como objetos Kotlin:

```kotlin
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object PlanDetail : Screen("plan_detail/{planId}") {
        fun createRoute(planId: Long) = "plan_detail/$planId"
    }
    // ...
}
```

Navegar a una pantalla es tan simple como:
```kotlin
navController.navigate(Screen.PlanDetail.createRoute(plan.id))
```

`NavGraph.kt` conecta cada ruta con su composable:
```kotlin
composable(Screen.Login.route) { LoginScreen(navController) }
composable("plan_detail/{planId}") { backStackEntry ->
    val planId = backStackEntry.arguments?.getString("planId")?.toLong()
    PlanDetailScreen(navController, planId)
}
```

Al arrancar la app, `NavGraph` comprueba si hay JWT guardado en DataStore:
- Hay JWT → navegar directamente a `MainScreen` (pantalla principal con el mapa)
- No hay JWT → navegar a `LoginScreen`

---

## 6. Comunicación entre Android y el backend: Retrofit2

**Retrofit2** es una librería que convierte las llamadas HTTP en funciones Kotlin simples.

En lugar de programar a mano el socket TCP, la conexión HTTP, el parseo del JSON... simplemente defines una interfaz:

```kotlin
interface ApiService {
    @GET("api/planes")
    suspend fun getPlanes(): Response<List<PlanDto>>

    @POST("api/planes")
    suspend fun crearPlan(@Body body: CrearPlanDto): Response<PlanDto>

    @DELETE("api/planes/{id}/abandonar")
    suspend fun abandonarPlan(@Path("id") id: Long): Response<Void>
}
```

Y Retrofit genera toda la implementación automáticamente. `@Body` serializa el objeto Kotlin a JSON usando **Gson**. `@Path` sustituye `{id}` por el valor real en la URL.

`ApiClient.kt` configura Retrofit una sola vez (es un `object`, equivalente a un singleton):

```kotlin
object ApiClient {
    private const val BASE_URL = "https://kdd-backend.onrender.com/"
    var jwtToken: String? = null   // ← se actualiza tras el login

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)           // ← OkHttp con el interceptor JWT
            .addConverterFactory(GsonConverterFactory.create())  // ← JSON ↔ Kotlin
            .build()
            .create(ApiService::class.java)
    }
}
```

`suspend fun` significa que la función es una **corrutina**: se puede pausar mientras espera la respuesta de red sin bloquear el hilo principal (la UI no se congela).

---

## 7. El backend por dentro: de la petición a la respuesta

Vamos a seguir una petición completa: `GET /api/planes` (cargar lista de planes).

### 7.1. Llega la petición

```
Android envía:
GET https://kdd-backend.onrender.com/api/planes
Authorization: Bearer eyJhbGci...
```

### 7.2. Pasa por el JwtAuthFilter

Spring ejecuta el filtro antes de que llegue al controlador:
1. Lee la cabecera `Authorization`
2. Extrae el token (quita "Bearer ")
3. `JwtService.isTokenValid(token)` → parsea el JWT, comprueba firma y fecha de expiración
4. Si es válido, extrae el `userId` y lo mete en `SecurityContextHolder`
5. La petición continúa

### 7.3. Llega al Controller

```java
@GetMapping("/api/planes")
public ResponseEntity<List<PlanDto>> getPlanes() {
    Long userId = (Long) SecurityContextHolder.getContext()
                         .getAuthentication().getPrincipal();
    List<Plan> planes = planRepository.findPlanesFuturos();
    List<PlanDto> dto = planes.stream()
                              .map(this::toDto)
                              .collect(toList());
    return ResponseEntity.ok(dto);
}
```

### 7.4. El Repository consulta MySQL

```java
// PlanRepository.java (Spring Data JPA)
@Query("SELECT p FROM Plan p WHERE p.fechaEvento >= CURRENT_DATE ORDER BY p.fechaEvento")
List<Plan> findPlanesFuturos();
```

Spring Data JPA convierte esto en SQL y lo envía a MySQL. MySQL devuelve las filas. Hibernate las convierte en objetos `Plan`.

### 7.5. Se convierte a DTO y se devuelve

El controlador convierte los objetos `Plan` (entidades JPA) en objetos `PlanDto` (solo los campos necesarios para el cliente) y los serializa a JSON. Gson en Android los deserializa a `List<PlanDto>`.

**¿Por qué DTOs y no devolver la entidad directamente?** Porque la entidad tiene relaciones JPA que pueden causar bucles infinitos al serializar, y puede exponer datos internos que el cliente no debe ver.

---

## 8. Base de datos: modelo de datos y triggers

### 8.1. Las tablas principales

| Tabla | Descripción |
|---|---|
| `usuarios` | id, nombre, email, password (hash), google_id, nombre_usuario (alias), fecha_nacimiento, proveedor |
| `planes` | id, titulo, descripcion, fecha_evento, hora_evento, hora_hasta, latitud, longitud, edad_min, edad_max, num_max_personas, creador_id, categoria_id |
| `categorias` | id, tipo (Deportes, Fiesta, Naturaleza...) |
| `participaciones` | id_usuario + id_plan (clave compuesta), estado (pendiente/confirmado), presente, fecha_union |
| `comunidades` | id, nombre, descripcion, edad_min, edad_max, admin_id |
| `pertenencias_comunidad` | id_usuario + id_comunidad, estado, mensaje_solicitud |
| `mensajes` | id, contenido, fecha_envio, emisor_id, receptor_id |
| `amistades` | id_usuario1 + id_usuario2 (clave compuesta), estado |
| `valoraciones` | id_evaluador + id_evaluado + id_plan (clave ternaria), puntuacion, comentario |

### 8.2. Cómo Hibernate crea las tablas

En `application.yaml` hay `ddl-auto: update`. Cuando Spring arranca, Hibernate:
1. Lee todas las clases `@Entity` del paquete `model/`
2. Compara con el esquema actual de MySQL
3. Crea las tablas que faltan o añade columnas nuevas (nunca borra)

Por eso **no necesitas crear las tablas a mano**: la primera vez que arrancas el backend con una base de datos vacía, Hibernate lo crea todo.

### 8.3. Triggers de MySQL

Los **triggers** son procedimientos SQL que MySQL ejecuta automáticamente cuando ocurre un evento (INSERT, UPDATE, DELETE) en una tabla. Se usan para aplicar reglas de negocio directamente en la base de datos, como una segunda capa de validación por encima del backend.

`TriggerInitializer.java` los crea al arrancar Spring Boot (si no existen):

| Trigger | Cuándo se ejecuta | Qué hace |
|---|---|---|
| `validar_edad_participacion` | BEFORE INSERT en `participaciones` | Bloquea si la edad del usuario no está en el rango del plan (el creador queda exento) |
| `control_plazas` | BEFORE INSERT en `participaciones` | Bloquea si el plan ya está lleno (num_max_personas alcanzado) |
| `auto_inscripcion_creador` | AFTER INSERT en `planes` | Inscribe automáticamente al creador con estado "confirmado" |
| `impedir_abandono_organizador` | BEFORE DELETE en `participaciones` | Bloquea si el usuario que intenta salir es el creador del plan |
| `validar_edad_comunidad` | BEFORE INSERT en `pertenencias_comunidad` | Bloquea si la edad no está en el rango de la comunidad |
| `auto_inscripcion_comunidad` | AFTER INSERT en `comunidades` | Inscribe automáticamente al creador como miembro y admin |
| `control_valoraciones` | BEFORE INSERT en `valoraciones` | Verifica que el campo `presente = true` antes de permitir la valoración |
| `validar_fecha_plan` | BEFORE INSERT en `planes` | Bloquea si la fecha del evento es en el pasado |

El `TriggerInitializer` detecta si el perfil activo es `prod` (PostgreSQL en Render) o el perfil por defecto (MySQL local) y usa la sintaxis SQL correcta para cada uno.

---

## 9. Firebase: configuración técnica

### 9.1. Dos partes de Firebase

Hay dos "lados" de Firebase en el proyecto que no deben confundirse:

**Firebase en Android (cliente):**
- Se configura con el archivo `google-services.json` (en `KDD_frontend/app/`)
- Este archivo contiene el `client_id`, el nombre del proyecto Firebase, etc.
- El SDK de Firebase en Android gestiona el Google Sign-In y produce el `idToken`

**Firebase Admin SDK en el backend (servidor):**
- Se configura con `firebase-service-account.json` (en `kdd_backend/src/main/resources/`)
- Este archivo contiene la clave privada del proyecto Firebase (¡nunca a GitHub!)
- Permite al backend llamar a `FirebaseAuth.getInstance().verifyIdToken(idToken)` para verificar que el token es auténtico

### 9.2. Por qué las credenciales no van a GitHub

- `google-services.json`: contiene claves de API. Si alguien las tiene, puede usar tu proyecto Firebase.
- `firebase-service-account.json`: es la clave privada del servidor. Con ella, alguien podría suplantar tu backend.
- `.gitignore` las excluye explícitamente.

### 9.3. Cómo funciona en producción (Render)

Como `firebase-service-account.json` no está en GitHub, en Render no existe ese archivo. La solución implementada en `FirebaseConfig.java`:

```java
// Primero intenta cargar el archivo local
serviceAccount = getClass().getClassLoader()
                .getResourceAsStream("firebase-service-account.json");

// Si no existe (producción), lee la variable de entorno FIREBASE_CREDENTIALS_JSON
if (serviceAccount == null) {
    String credencialesBase64 = System.getenv("FIREBASE_CREDENTIALS_JSON");
    if (credencialesBase64 != null) {
        byte[] bytes = Base64.getDecoder().decode(credencialesBase64);
        serviceAccount = new ByteArrayInputStream(bytes);
    }
}
```

En Render, el archivo JSON está codificado en Base64 y guardado como variable de entorno. Al arrancar, el backend lo decodifica y lo usa.

---

## 10. Render: cómo está configurado el despliegue

**Render** detecta automáticamente que es un proyecto Maven (por el `pom.xml`) y lo construye con:
```
mvn clean package -DskipTests
```

Luego ejecuta el JAR resultante:
```
java -jar target/kdd-backend-0.0.1-SNAPSHOT.jar
```

### Variables de entorno en Render

En lugar de hardcodear valores sensibles en `application.yaml`, se usan variables de entorno. En `application.yaml`:

```yaml
datasource:
  url: ${DB_URL:jdbc:mysql://localhost:3306/kdd...}   # En local usa el valor por defecto
  username: ${DB_USERNAME:root}                         # En Render usa la variable de entorno
  password: ${DB_PASSWORD:1234}
```

En el panel de Render se configuran:
- `DB_URL` → URL de la base de datos en la nube
- `DB_USERNAME`, `DB_PASSWORD` → credenciales de MySQL en Render
- `JWT_SECRET` → clave secreta para firmar JWT (larga y aleatoria)
- `FIREBASE_CREDENTIALS_JSON` → el JSON de credenciales en Base64
- `GOOGLE_CLIENT_ID` → Client ID de Google Cloud para verificar tokens

### El perfil `prod`

En Render se activa el perfil Spring `prod` con:
```
SPRING_PROFILES_ACTIVE=prod
```

Esto hace que `TriggerInitializer` use la sintaxis de PostgreSQL en lugar de MySQL (Render usa PostgreSQL en el plan gratuito).

---

## 11. DataStore: persistencia local en Android

**Jetpack DataStore** es la forma moderna de guardar pequeñas cantidades de datos en el dispositivo Android (sustituto de SharedPreferences).

En `TokenDataStore.kt`:

```kotlin
object TokenDataStore {
    private val KEY_JWT     = stringPreferencesKey("jwt_token")
    private val KEY_USER_ID = longPreferencesKey("user_id")
    // ...

    suspend fun saveSession(context, token, userId, displayName, email) {
        context.tokenDataStore.edit { prefs ->
            prefs[KEY_JWT]     = token
            prefs[KEY_USER_ID] = userId
            // ...
        }
        ApiClient.jwtToken = token  // ← también lo mete en memoria para las peticiones inmediatas
    }

    fun getToken(context): Flow<String?> =
        context.tokenDataStore.data.map { it[KEY_JWT] }

    suspend fun clearSession(context) {
        context.tokenDataStore.edit { it.clear() }
        ApiClient.jwtToken = null  // ← limpia también la memoria
    }
}
```

Cuando el usuario abre la app después de haberla cerrado, `NavGraph` lee el token del DataStore. Si existe y no ha expirado, va directamente a la pantalla principal sin pedir login.

---

## 12. Flujo completo de ejemplo: crear un plan

Vamos a seguir la acción de punta a punta:

```
1. El usuario rellena el formulario en CreatePlanScreen y pulsa "Terminar"
   ↓
2. CreatePlanScreen llama a planViewModel.crearPlan(titulo, categoria, fecha, ...)
   ↓
3. PlanViewModel crea un objeto CrearPlanDto con esos datos y llama a:
   ApiClient.api.crearPlan(body)
   (Esta es una suspend fun → se ejecuta en una corrutina sin bloquear la UI)
   ↓
4. OkHttp interceptor añade "Authorization: Bearer <JWT>" a la petición
   ↓
5. POST https://kdd-backend.onrender.com/api/planes
   Body: { "titulo": "Escalada Madrid", "fechaEvento": "2026-07-15", ... }
   ↓
6. JwtAuthFilter en el servidor: extrae userId del JWT y lo mete en SecurityContextHolder
   ↓
7. PlanController.crearPlan() recibe la petición:
   - Lee el userId del SecurityContextHolder → sabe quién es el creador
   - Busca la categoría en CategoriaRepository (o la crea si no existe)
   - Construye el objeto Plan con todos los campos
   - Llama a planRepository.save(plan) → Hibernate hace INSERT en tabla `planes`
   ↓
8. TRIGGER: auto_inscripcion_creador se dispara AFTER INSERT en planes
   → INSERT en participaciones (creador, plan, estado='confirmado')
   ↓
9. El backend devuelve 201 Created con el PlanDto del plan recién creado
   ↓
10. En el ViewModel: response.isSuccessful → navegar a PlanDetailScreen del nuevo plan
    ↓
11. La pantalla muestra el detalle del plan recién creado con 1 participante (el creador)
```

---

## 13. Resumen: cuándo habla cada parte con quién

| Quién | Habla con | Para qué |
|---|---|---|
| Android (Firebase SDK) | Firebase (Google) | Obtener el idToken de Google Sign-In |
| Android (Retrofit2) | Backend (Render) | Todo lo demás: planes, usuarios, comunidades, chat |
| Backend (Firebase Admin SDK) | Firebase (Google) | Verificar que el idToken es auténtico (solo en login) |
| Backend (Spring Data JPA) | MySQL | Leer y guardar todos los datos de la app |
| Backend (TriggerInitializer) | MySQL | Crear los triggers de validación al arrancar |
| MySQL (triggers) | — | Aplicar restricciones automáticamente en cada INSERT/DELETE |

---

## 14. Preguntas típicas de defensa

**¿Por qué no usas Firebase para guardar los datos en lugar de MySQL?**
Firebase Firestore es una base de datos NoSQL sin esquema fijo. El modelo de KDD tiene relaciones complejas (N:M, ternarias) que se modelan mucho mejor con SQL. Además, tener el backend propio en Spring Boot da más control sobre la lógica de negocio.

**¿Por qué JWT y no sesiones con cookies?**
Las sesiones de servidor guardan estado. Una API REST es stateless (sin estado): cada petición es independiente. Con JWT, el servidor no necesita recordar nada: toda la información está en el token. Además, escala bien: varios servidores pueden validar el mismo JWT sin compartir sesiones.

**¿Qué pasa si el JWT expira?**
El usuario tendrá que hacer login de nuevo. El backend devuelve 401 Unauthorized y el frontend redirige a la pantalla de login. Una mejora futura sería implementar refresh tokens.

**¿Por qué Render y no el móvil como servidor?**
El backend debe ser accesible desde cualquier red, no solo la local. Render proporciona una URL pública con HTTPS.

**¿Cómo garantizas que un usuario no puede borrar el plan de otro?**
En el controlador se compara `plan.getCreadorId()` con el `userId` extraído del JWT. Si no coinciden, se devuelve 403 Forbidden. El userId nunca viene del cliente, siempre del token firmado por el servidor.

**¿Qué es un trigger y por qué lo usas en lugar de validar en el backend?**
El trigger es una regla en la propia base de datos. Es la última línea de defensa: aunque alguien acceda directamente a MySQL saltándose el backend, las restricciones siguen activas. Es una buena práctica de diseño de bases de datos.

**¿Qué es Jetpack Compose?**
Un sistema declarativo para crear interfaces Android. En lugar de inflar XMLs y buscar vistas con `findViewById`, describes en código Kotlin cómo debe verse la pantalla en función del estado. Cuando el estado cambia, Compose redibuja automáticamente solo las partes afectadas (recomposición).

**¿Qué ventaja tiene MVVM?**
Separa responsabilidades: la pantalla no sabe nada de red ni de base de datos, y el ViewModel no sabe nada de cómo se ve la pantalla. Esto facilita el testing y el mantenimiento. Además, el ViewModel sobrevive a los cambios de configuración (rotación de pantalla).
