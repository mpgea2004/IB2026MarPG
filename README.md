# ⚡ Iberdrola Android - Prácticas 2026 (MarPG)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.x-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-2024.12.01-green.svg)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/Hilt-2.51.1-orange.svg)](https://dagger.dev/hilt/)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)

<p align="center">
  <img src="app/screenshots/portada.png" width="400" alt="Portada de la Aplicación">
</p>

Este repositorio contiene la solución técnica avanzada desarrollada para el programa de formación de **Iberdrola / Viewnext**. El proyecto no solo cumple con los hitos de las entregas oficiales, sino que integra módulos de valor añadido bajo estándares de industria.

---

## 🏗️ Arquitectura y Stack Tecnológico

La aplicación se rige por los principios de **Clean Architecture** y **MVVM**, garantizando un desacoplamiento total entre la lógica de negocio y la interfaz de usuario.

### 🧬 Capas del Sistema
*   **`domain`**: Lógica de negocio pura (**Use Cases**) sin dependencias de frameworks.
*   **`data`**: Gestión de datos con **Retrofit** para API y **Room** como caché persistente (**SSOT**).
*   **`ui`**: Interfaces reactivas construidas íntegramente en **Jetpack Compose**.

### 🛠️ Especificaciones Técnicas
*   **Single Source of Truth (SSOT):** Room actúa como única fuente de verdad. La UI observa la DB, y la red actualiza la DB.
*   **Gestión de Estados:** Implementación de `StateFlow` y `SharedFlow` para una comunicación reactiva.
*   **Inyección de Dependencias:** **Hilt** (Dagger) para la gestión del ciclo de vida de los componentes.
*   **Persistencia Híbrida:**
    *   **Room:** Para datos complejos y relacionales (Facturas, Contratos).
    *   **DataStore (Preferences):** Gestión de estados atómicos (conteo de feedback, sesión de usuario).

---

## 📂 Estructura del Proyecto

```text
app/src/main/java/com/iberdrola/.../MarPG/
├── data/           # Implementaciones de Repositorios, API y DB
│   ├── local/      # Room (Entities, DAOs, Database)
│   ├── network/    # Retrofit (Services, Exceptions)
│   └── mapper/     # Conversión DTO <-> Entity <-> Domain
├── di/             # Módulos de Hilt (AppModule, NetworkModule)
├── domain/         # Casos de Uso y Modelos de Negocio
├── ui/             # UI Components, Screens y ViewModels
└── MainApplication.kt
```

---

## 📱 Flujo de Navegación y Pantallas

Se ha diseñado un grafo de navegación optimizado que incluye pantallas adicionales para una gestión completa:

### Esquema de Navegación (Flow)
```mermaid
graph TD
    A[Home]
    A --> B[Listado de Facturas]
    B --> C[Filtros Avanzados]
    A --> D[Perfil de Usuario]
    D --> F[Botón Crashlytics]
    A --> E[Listado Factura Electrónica]
    E --> G[Detalle Factura Electrónica/activa]
    E --> H[Detalle Factura Electrónica/noactiva]
    H --> M[Activar Factura Electrónica]
    G --> I[Éxito Desactivar Factura Electrónica]
    G --> J[Modificar Email]
    J/M --> K[Código Verificación Factura Electrónica]
    K --> L[Éxito Factura Electrónica]
```

### Detalle de Pantallas

#### Home (Personalizada)
Es el centro de control principal. Ofrece accesos directos y una vista general para mejorar la usabilidad.
<img src="app/screenshots/home.png" width="250" alt="Pantalla Home">

#### Perfil de Usuario (Personalizada)
Sección para que el usuario gestione su número de teléfono y email, datos clave para la facturación electrónica.
<img src="app/screenshots/perfil_usuario.png" width="250" alt="Pantalla Perfil de Usuario">

#### Feed de Facturas
Lista principal de todas las facturas. Incluye efectos de carga (Shimmer) y responde a los filtros y la conexión de red.
<img src="app/screenshots/feed_facturas.png" width="250" alt="Pantalla Feed de Facturas">

#### Filtrado de Facturas
Pantalla con filtros avanzados para buscar facturas específicas.
<img src="app/screenshots/filtrado_facturas.png" width="250" alt="Pantalla Filtrado de Facturas">

#### Feed de Facturas Electrónicas
Muestra el estado de la facturación electrónica para los distintos contratos.
<img src="app/screenshots/feed_factura_electronica.png" width="250" alt="Pantalla Feed Facturas Electrónicas">

#### Detalle Factura Electrónica Activa
Muestra los datos y opciones de configuración de una factura electrónica activada.
<img src="app/screenshots/detalle_factura_electronica_activa.png" width="250" alt="Pantalla Detalle Factura Electrónica Activa">

#### Éxito Desactivar Factura Electrónica (Personalizada)
Mensaje de confirmación que aparece tras desactivar correctamente el servicio.
<img src="app/screenshots/exito_desactivar_factura_electronica.png" width="250" alt="Pantalla Éxito Desactivar Factura Electrónica">

#### Edición Email de Factura Electrónica Activa
Formulario sencillo para cambiar la dirección de correo electrónico.
<img src="app/screenshots/edicion_email_factura_electronica.png" width="250" alt="Pantalla Edición Email Factura Electrónica">

#### Activación de Factura Electrónica
Proceso inicial para dar de alta el servicio en un contrato.
<img src="app/screenshots/activacion_factura_electronica.png" width="250" alt="Pantalla Activación Factura Electrónica">

#### Código Verificación de Factura Electrónica
Pantalla de seguridad para introducir el código recibido (SMS/Email).
<img src="app/screenshots/codigo_verificacion_factura_electronica.png" width="250" alt="Pantalla Código Verificación Factura Electrónica">

#### Éxito de Factura Electrónica
Mensaje final que confirma que el proceso se ha completado con éxito.
<img src="app/screenshots/exito_factura_electronica.png" width="250" alt="Pantalla Éxito Factura Electrónica">

---

## 🌟 Funcionalidades de Valor Añadido
#### Gestión de Facturación Electrónica
Implementación de un flujo completo de activación, modificación y **desactivación**. Incluye lógica de validación mediante código de verificación.

#### Switch de Conectividad
Selector de modo para forzar el Modo Offline, permitiendo validar la robustez de la caché local.

#### Feedback Inteligente
Uso de DataStore para persistir el número de visualizaciones del diálogo de feedback.

#### Gestión de Sesión de Usuario (DataStore)
Uso de **Preferences DataStore** para gestionar un perfil de usuario reactivo. Las pantallas reaccionan instantáneamente a cambios en el email o teléfono.

---

## 🚀 Instalación y Configuración

1.  **Clonar repositorio:**
    ```bash
    git clone https://github.com/mpgea2004/IB2026MarPG.git
    ```
2.  **Configurar Firebase:** Añadir el archivo `google-services.json` en la carpeta `app/`.
3.  **Sincronizar:** Abrir con Android Studio Ladybug o superior y sincronizar Gradle.
4.  **Ejecutar:** `./gradlew assembleDebug`

---

## 🧪 Estrategia de Testing

*   **Unit Tests (test):** Validación de lógica en Use Cases (ej. `FormatUserPhoneUseCaseTest`), validadores y Mappers.
*   **Android Tests (androidTest):** Pruebas de integración para validar operaciones CRUD en Room (ej. `UserDaoTest`).

---

## 📊 Monitorización (Cuarta Entrega)
*   **Google Analytics:** Tracking de navegación y registro de eventos (`filter_applied`, etc.).
*   **Crashlytics:** Reporte de errores. Se incluye un botón de fallo forzado en la pantalla de Perfil.
*   **Remote Config:** Configuración dinámica para el filtrado de contratos de Gas.

---

## ✒️ Desarrollado por
**MarPG** - Prácticas de Especialización Android 2026  (Viewnext)
