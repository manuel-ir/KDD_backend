# Estructura Exposición KDD — 25 minutos

> **Normas clave recordatorio:**
> - Sin herramientas de IA con marca de agua (no Gamma)
> - Colores coherentes con la paleta de la app
> - Sin tecnicismos de código, sin diagramas de flujo
> - Demo en vivo integrada durante la exposición, no al final
> - Mencionar pruebas de usabilidad
> - Generalizar el contenido, ser específico solo en los puntos clave

---

## BLOQUE 1 — El problema y la solución (3 min)

**Objetivo:** Enganchar al tribunal desde el primer segundo.

**Diapositiva: "¿Cuántas veces has dicho 'quedamos' y no ha pasado nada?"**

- Contexto: organizar planes en grupo es caótico. Los grupos de WhatsApp se saturan, nadie confirma, se pierde la información.
- ¿Qué ofrecen las apps actuales?
  - **Meetup**: orientado a comunidades, en inglés, interfaz compleja, sin chat directo integrado
  - **Facebook Eventos**: público general, privacidad cuestionable, demografía mayor
  - **WhatsApp**: no descubres gente nueva, no hay estructura de plan, no hay confirmaciones formales
- **KDD** lo integra todo en un solo sitio: crea el plan, gestiona quién va, organízate en comunidades y habla con los participantes

**Diapositiva: propuesta de valor en 3 puntos** (visual, iconos, sin texto largo)
1. Descubrir planes cerca de ti por categoría y mapa
2. Organizarte con personas afines en comunidades
3. Comunicarte y confirmar asistencia dentro de la misma app

---

## BLOQUE 2 — La app en un vistazo (2 min)

**Objetivo:** Dar contexto visual antes de la demo. Una sola diapositiva.

**Diapositiva: mockup/captura del mapa principal con planes**

- KDD está disponible en Android
- Desarrollada en Kotlin con Jetpack Compose (tecnología oficial de Google para Android)
- Conectada a un servidor en la nube que funciona 24/7
- Autenticación con Google en un solo tap (o con email y contraseña)

> *(Breve, ir rápido — el detalle técnico viene en el siguiente bloque)*

---

## BLOQUE 3 — Tecnología usada y por qué (3 min)

**Objetivo:** Justificar las decisiones técnicas sin entrar en código.

**Diapositiva: diagrama visual de 3 capas (sin código, solo nombres e iconos)**

```
[App Android]  →  [Servidor Spring Boot en Render]  →  [Base de datos MySQL]
                           ↕
                    [Firebase / Google]
```

| Tecnología | Por qué se eligió | Por qué no otra |
|---|---|---|
| Kotlin + Jetpack Compose | Estándar actual de Google, interfaces modernas con menos código | Java (más verboso, tecnología en declive en Android) |
| Spring Boot | Framework más usado en empresas para APIs, muy buena documentación | Node.js (menos tipado, más difícil de escalar con lógica compleja) |
| MySQL | Base de datos relacional, perfecta para relaciones complejas entre entidades | Firebase Firestore (NoSQL, mal encaje con relaciones N:M como las del modelo) |
| Firebase Auth | Delega la seguridad de Google Sign-In a Google mismo | Construir autenticación propia desde cero (riesgo de seguridad innecesario) |
| Render | Despliegue en la nube gratuito, permite que la app funcione desde cualquier red | Servidor local (no accesible fuera de casa) |

**Punto clave a mencionar:** la app no es un prototipo local — está desplegada en producción y accesible desde cualquier dispositivo con internet.

---

## BLOQUE 4 — Demo en vivo: funcionalidades principales (12 min)

> Este es el bloque central. Cada sub-sección tiene una diapositiva de apoyo pero la acción ocurre en el móvil.

### 4.1 — Registro e inicio de sesión (1.5 min)
- **En vivo:** mostrar Google Sign-In en un tap → llega a la pantalla principal
- **Diapositiva de apoyo:** captura del login + texto "Autenticación segura con Google"
- Mencionar: también funciona con email/contraseña con validación de requisitos

### 4.2 — Explorar y unirse a planes (2.5 min)
- **En vivo:** abrir Explorar → mapa con planes cerca → pulsar un plan → ver detalle → unirse
- **Diapositiva de apoyo:** captura del mapa con emojis de categorías
- Destacar: filtros por categoría, edad, fecha — el plan muestra aforo, idioma, edad mínima/máxima
- Mencionar: si el plan está lleno o el usuario no cumple la edad, no puede unirse (reglas automáticas)

### 4.3 — Crear un plan (2 min)
- **En vivo:** crear un plan en vivo (prepararlo previamente con datos para ir rápido)
- **Diapositiva de apoyo:** captura del formulario de creación
- Destacar: geolocalización con Google Maps, categorías, límite de personas, rango de edad

### 4.4 — Comunidades (2 min)
- **En vivo:** entrar a una comunidad → ver miembros → crear plan interno de comunidad
- **Diapositiva de apoyo:** captura de la pantalla de comunidad
- Destacar: las comunidades tienen sus propios planes privados, solicitud de entrada, admin gestiona miembros

### 4.5 — Chat y asistencia (2 min)
- **En vivo:** abrir chat con un usuario → enviar mensaje → marcar asistencia en un plan
- **Diapositiva de apoyo:** captura del chat
- Destacar: el chat es directo entre usuarios, el sistema de asistencia permite saber quién fue realmente al plan

### 4.6 — Perfil y valoraciones (2 min)
- **En vivo:** ver perfil propio → editar bio → (mostrar que existe valoración tras asistir)
- **Diapositiva de apoyo:** captura del perfil
- Destacar: la reputación se construye con las valoraciones de otros participantes

---

## BLOQUE 5 — Usabilidad (1.5 min)

**Diapositiva: "¿Es fácil de usar?"**

- La app fue probada con usuarios reales (compañeros, amigos) para detectar fricciones
- Feedback recibido: mencionar 1-2 cambios concretos que se hicieron tras las pruebas (ej: simplificar el formulario de creación de plan, añadir mensajes de error claros)
- Navegación por tabs en la parte inferior → acceso directo a las secciones principales en 1 tap
- Google Sign-In elimina el formulario de registro para la mayoría de usuarios

> *(Si la app es responsive mencionar aquí: "la interfaz se adapta a distintos tamaños de pantalla")*

---

## BLOQUE 6 — Estado actual: lista para el mundo (1 min)

**Diapositiva: "KDD ya está en producción"**

- El servidor lleva funcionando en Render con X usuarios registrados (mostrar captura de Firebase Console con número real de usuarios)
- La base de datos tiene datos reales de prueba
- Funcionalidad completa: login, planes, comunidades, chat, valoraciones, mapa
- Es un proyecto vivo — la arquitectura está pensada para escalar

---

## BLOQUE 7 — Mejoras futuras (2.5 min)

> Enfocar en mejoras atractivas para el usuario final, no en "lo que no dio tiempo".

**Diapositiva: "¿Hacia dónde va KDD?"**

Las 5 mejoras más impactantes:

1. **Notificaciones push en tiempo real** — que te avise cuando alguien confirma asistencia, te manda un mensaje o acepta tu solicitud a una comunidad

2. **Integración con calendarios** — exportar un plan directamente a Google Calendar o Apple Calendar con un tap

3. **Planes recurrentes y plantillas** — si haces running cada semana, crear el plan una vez y que se repita automáticamente

4. **Sistema de reputación visible** — que el perfil muestre públicamente la media de valoraciones, creando un incentivo para comportarse bien en los planes

5. **Modo social avanzado** — sugerencia automática de planes basada en tus categorías favoritas y el historial de asistencias ("planes que te pueden interesar")

Bonus mencionable: versión web para organizar planes desde el ordenador (sin instalar nada).

---

## BLOQUE 8 — Cierre (1 min)

**Diapositiva final: nombre de la app + QR para descargar / repositorio**

- Resumen en una frase: *"KDD centraliza todo lo que necesitas para organizar y vivir planes grupales, desde el descubrimiento hasta la valoración final"*
- Invitar a preguntas

---

## Timing total

| Bloque | Minutos |
|---|---|
| 1. El problema y la solución | 3:00 |
| 2. La app en un vistazo | 2:00 |
| 3. Tecnología usada y por qué | 3:00 |
| 4. Demo en vivo | 12:00 |
| 5. Usabilidad | 1:30 |
| 6. Estado actual | 1:00 |
| 7. Mejoras futuras | 2:30 |
| 8. Cierre | 1:00 |
| **TOTAL** | **27:00** |

> Hay 2 minutos de margen para imprevistos técnicos o preguntas durante la demo.
> Si algo falla en el móvil, las capturas en las diapositivas sirven de respaldo.

---

## Pendiente antes de hacer las diapositivas

- [ ] Decidir qué capturas de pantalla van en cada bloque (ya las tienes todas)
- [ ] Conseguir el número real de usuarios registrados en Firebase Console
- [ ] Preparar un usuario de demo con datos reales (planes creados, comunidades, chat)
- [ ] Decidir la paleta de colores exacta de la app para aplicarla a las diapositivas
- [ ] Elegir la herramienta para hacer las diapositivas (Canva recomendado, sin watermark, sin IA generativa visible)
