# FerovaFamily

Aplicación móvil Android para apoyar el tratamiento contra la anemia infantil en el Perú, dirigida a padres y cuidadores. Forma parte del ecosistema **Ferova / Sanuvi**, junto con FerovaClinic (app para enfermeras del MINSA) y un backend en Spring Boot.

> Proyecto académico — Curso de Aplicaciones Móviles (1ACC0238), UPC.

---

## Tabla de contenidos

- [Descripción](#descripción)
- [Funcionalidades](#funcionalidades)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Configuración inicial](#configuración-inicial)
- [Dependencias principales](#dependencias-principales)
- [Convenciones de código](#convenciones-de-código)
- [Flujo de Git](#flujo-de-git)
- [Equipo](#equipo)

---

## Descripción

FerovaFamily acompaña al cuidador (madre, padre o tutor) durante el tratamiento de anemia del menor a su cargo, mediante:

- **Recordatorios y seguimiento de dosis** de hierro.
- **Comunicación directa con la enfermera asignada** del MINSA.
- **Reserva de citas** en postas y centros de salud cercanos.
- **Sistema de gamificación** con medallas y rachas para incentivar la adherencia.
- **Visualización del progreso** clínico (evolución de hemoglobina) y nutricional.

---

## Funcionalidades

### Implementadas

- Autenticación: login, registro, recuperación de contraseña con verificación.
- Pantalla principal con bottom bar de 4 secciones (Inicio, Diario, Citas, Consultas).
- **Módulo Consultas**
  - Estado vacío cuando aún no hay enfermera asignada.
  - Lista de hijos para iniciar consulta.
  - Bandeja de "Mis Consultas" activas.
  - Chat con la enfermera asignada.
- **Módulo Citas / Postas Cercanas**
  - Mapa interactivo de centros de salud (OpenStreetMap).
  - Detalle de posta con días de atención, contacto y servicios.
  - Reserva con selección de paciente, fecha y horario.
  - Pantalla de confirmación de cita.
- **Módulo Progreso y Medallas**
  - Estado de salud, puntos totales y rachas.
  - Gráfico de evolución de hemoglobina (Canvas Compose).
  - Listado de medallas con barra de progreso.
  - Pantalla de "Dosis Confirmada" con próxima medalla.
  - Pantalla de "Medalla Desbloqueada" (celebración).
  - Pantalla de "Racha Perdida".

### En desarrollo

- Sección Diario (registro de alimentación).
- Pantalla detallada de Citas activas.
- Integración con backend Spring Boot.
- Notificaciones push (Firebase Cloud Messaging).

---

## Stack tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navegación:** Navigation Compose (NavHost anidado: auth + main con bottom bar)
- **Mapas:** osmdroid (OpenStreetMap)
- **Persistencia local:** Room *(planificado)*
- **Networking:** Retrofit + OkHttp *(planificado)*
- **Mensajería:** Firebase Cloud Messaging *(planificado)*
- **JDK objetivo:** 17
- **minSdk:** 26 — **targetSdk:** 35

---

## Arquitectura

El proyecto sigue una arquitectura **MVVM** organizada por features:

```
UI (Compose Screens)
        │
        ▼
ViewModel (StateFlow + UiState)
        │
        ▼
Repository  ──►  Local (Room) / Remote (Retrofit)
```

Cada módulo (consultations, appointments, progress) tiene su propio paquete con su `ViewModel`, `Routes`, `model/`, `screens/` y `components/`.

La navegación está organizada en dos niveles:

1. **NavGraph principal** — pantallas de autenticación + entrada al área principal.
2. **MainScreen NavHost** — scaffold con bottom bar que envuelve los 4 tabs y todas las subpantallas.

---

## Estructura del proyecto

```
app/src/main/java/pe/edu/upc/ferovafamily/
├── presentation/
│   ├── auth/                    # Login, CreateAccount, FerovaTextField
│   ├── shared/                  # RecoveryPassword, Verification, NewPassword
│   ├── home/                    # HomeScreen (placeholder + accesos rápidos)
│   ├── main/                    # MainScreen, BottomNavItem, MainRoutes
│   ├── navigation/              # NavGraph principal (auth + main)
│   ├── consultations/
│   │   ├── model/
│   │   ├── components/
│   │   ├── screens/
│   │   ├── ConsultationsViewModel.kt
│   │   └── ConsultationsRoutes.kt
│   ├── appointments/
│   │   ├── model/
│   │   ├── components/          # OsmMapView
│   │   ├── screens/
│   │   ├── AppointmentsViewModel.kt
│   │   └── AppointmentsRoutes.kt
│   ├── progress/
│   │   ├── model/
│   │   ├── components/          # HemoglobinChart, MedalListItem
│   │   ├── screens/
│   │   ├── ProgressViewModel.kt
│   │   └── ProgressRoutes.kt
│   └── theme/                   # Color, Theme, Type
└── MainActivity.kt
```

---

## Configuración inicial

### Requisitos

- Android Studio Ladybug o superior
- JDK 17
- Dispositivo físico o emulador con API 26+

### Pasos

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/SANUVI-MINSA/FerovaFamily.git
   ```
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle (automático al abrir).
4. Ejecutar en dispositivo/emulador.

> No requiere API keys externas. El mapa usa OpenStreetMap (libre y gratuito).

---

## Dependencias principales

```kotlin
// Compose + Material 3
implementation(platform("androidx.compose:compose-bom:2024.10.00"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material:material-icons-extended")

// Navegación
implementation("androidx.navigation:navigation-compose:2.8.4")

// ViewModel + Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

// Mapa (OpenStreetMap)
implementation("org.osmdroid:osmdroid-android:6.1.18")
```

---

## Convenciones de código

- **Naming:** clases en `PascalCase`, variables en `camelCase`, constantes en `SCREAMING_SNAKE_CASE`, paquetes en `lowercase`.
- **Booleanos:** prefijo `is`, `has` o `should` (`isUnlocked`, `hasNurse`).
- **Composables:** verbo o sustantivo en PascalCase (`HomeScreen`, `MedalListItem`).
- **UI State:** clases data con sufijo `UiState` (`ConsultationsUiState`).
- **Rutas:** objetos con constantes en `SCREAMING_SNAKE_CASE` y funciones helper para parámetros.
- **Strings de UI:** en español (idioma del usuario final). Identificadores y código: en inglés.
- **Colores:** definidos en `theme/Color.kt`. Paleta principal:
  - Crimson `#8B1A1A`
  - Cream `#FDF8F8`
  - SoftPink `#F9E8E8`

---

## Flujo de Git

El repositorio sigue **GitFlow** con **Conventional Commits**.

### Ramas

- `main` — versiones estables liberadas.
- `develop` — rama de integración del equipo.
- `feature/<nombre>` — nuevas funcionalidades.
- `fix/<nombre>` — corrección de bugs.

### Formato de commits

```
<tipo>(<scope>): <descripción corta>

[cuerpo opcional]
```

**Tipos:** `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`.

**Ejemplos:**
```
feat(consultations): add chat screen with message bubbles
fix(navigation): prevent duplicate route on tab re-selection
refactor(progress): rename achievements module to progress
docs(readme): update project structure
```

---

## Licencia

Proyecto de uso académico — UPC 2026.
