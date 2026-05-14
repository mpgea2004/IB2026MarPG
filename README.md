# Viewnext App Android Iberdrola - Prácticas 2026 (MarPG)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.x-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-2024.12.01-green.svg)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/Hilt-2.51.1-orange.svg)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Room-2.6.x-red.svg)](https://developer.android.com/training/data-storage/room)
[![Firebase](https://img.shields.io/badge/Firebase-BOM-yellow.svg)](https://firebase.google.com)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)

<p align="center">
  <img src="app/screenshots/portada.png" width="400" alt="Portada de la Aplicación">
</p>

Solución técnica avanzada desarrollada para el programa de formación de Viewnext. Esta aplicación implementa una de gestión de facturas y contratos de energía, integrando arquitectura limpia, patrones de diseño avanzados y amplia instrumentación de monitorización.

---

## Arquitectura y Stack Tecnológico

La aplicación sigue los principios de Clean Architecture con patrón MVVM para garantizar escalabilidad, mantenibilidad y testabilidad.

### Capas del Sistema
- **domain**: Lógica de negocio pura (Use Cases) sin dependencias de frameworks
- **data**: Gestión de datos con Retrofit (API REST) y Room (caché persistente)
- **ui**: Interfaces reactivas en Jetpack Compose

### Stack Tecnológico
- Language: Kotlin 1.9.x
- UI Framework: Jetpack Compose 2024.12.01
- DI Container: Hilt 2.51.1 (Dagger)
- Base Datos: Room 2.6.x
- Network: Retrofit 2.11.x + Gson
- State Management: StateFlow / SharedFlow
- Local Storage: DataStore Preferences
- Analytics: Google Analytics
- Crash Reporting: Firebase Crashlytics
- Remote Config: Firebase Remote Config

### Patrones y Principios
- Single Source of Truth (SSOT): Room actúa como única fuente de verdad
- Reactive Programming: Flows para comunicación asíncrona
- Dependency Injection: Hilt para gestión del ciclo de vida
- Repository Pattern: Abstracción de fuentes de datos
- Use Case Pattern: Encapsulación de lógica de negocio

---


## Estructura del Proyecto

```
app/src/main/java/com/iberdrola/practicas2026/MarPG/
├── MainActivity.kt
├── MainApplication.kt
├── permissions/
├── di/
│   ├── AppModule.kt
│   └── NetworkModule.kt
├── domain/
│   ├── model/
│   │   ├── Invoice.kt
│   │   ├── ElectronicInvoice.kt
│   │   ├── ContractType.kt
│   │   └── InvoiceStatus.kt
│   ├── use_case/
│   │   ├── invoice/
│   │   │   └── GetInvoiceUseCase.kt
│   │   ├── contracts/
│   │   ├── events/
│   │   ├── feedback/
│   │   └── users/
│   ├── repository/
│   │   ├── InvoiceRepository.kt
│   │   └── ElectronicInvoiceRepository.kt
│   └── utils/
├── data/
│   ├── network/
│   │   ├── InvoiceApiServer.kt
│   │   ├── ElectronicInvoiceApiService.kt
│   │   └── InvoiceException.kt
│   ├── local/
│   │   ├── database/
│   │   ├── dao/
│   │   ├── entities/
│   │   └── preferences/
│   ├── mapper/
│   ├── dto/
│   └── repository/
└── ui/
    ├── theme/
    ├── home/
    ├── factura_home/
    ├── factura_list/
    ├── factura_detail/
    ├── factura_filter/
    ├── user_profile/
    ├── electronic_invoice_selection/
    ├── electronic_invoice_detail/
    ├── consumption_dashboard/
    ├── faq/
    ├── components/
    └── utils/
```

---

## Pantallas y Flujo de Navegación

### Detalle de Pantallas

#### Home (Personalizada)
Centro de control principal con accesos rápidos a facturas, perfil y facturación electrónica. Incluye selector de origen de datos (API/Local) y feedback sheet.

Archivo: `ui/factura_home/HomeScreen.kt`

#### Perfil de Usuario (Personalizada)
Sección para gestionar datos personales: nombre, email, teléfono y contraseña. Pudiendolos editar pidiendo siempre la contraseña.

Archivo: `ui/user_profile/ProfileScreen.kt`

#### Listado de Facturas
Lista principal de todas las facturas con efectos de carga (Shimmer) y responde a filtros y conexión de red. Soporta tabs para Luz/Gas. Desde esta pantalla podemos activar/desactivar la visualización del importe de las facturas, acceder a filtrado, a detalle de factura y a mi consumo.

Archivo: `ui/factura_list/InvoiceListScreen.kt`

#### Filtros Avanzados
Pantalla con filtros avanzados para buscar facturas específicas por fecha, importe y estado.

Archivo: `ui/factura_filter/FilterScreen.kt`

#### Detalle de Factura
Pantalla con detalle de la factura en la que podemos activar/desactivar la visualización del importe de las facturas(relacionado con el de listado), podemos descargar la factura, copiar su número de factura y pagarla si es podible.

Archivo: `ui/factura_detail/InvoiceDetailScreen.kt`

#### Consumo
Pantalla con gráfico del consumo de la energía de los contratos.

#### Preguntas frecuentes
Pantalla con preguntas frecuentes de los usuarios y enlace a atención al cliente de iberdrola. Además ahí está escondido el botón de error para crashlytics.

Archivo: `ui/faq/FaqScreen.kt`t`

#### Selector de Contratos
Muestra el estado de la facturación electrónica para los distintos contratos disponibles.

Archivo: `ui/electronic_invoice_selection/ElectronicInvoiceListScreen.kt`

#### Detalle Factura Electrónica Activa
Muestra los datos y opciones de configuración de una factura electrónica activada. Permite editar email o desactivar el servicio.

Archivo: `ui/electronic_invoice_detail/ElectronicInvoiceDetailInfoScreen.kt`

#### Desactivación de Factura Electrónica (Personalizada)
Mensaje de confirmación que aparece tras desactivar correctamente el servicio.

Archivo: `ui/electronic_invoice_detail/SuccessScreen.kt`

#### Edición de Email
Formulario para cambiar la dirección de correo electrónico de envío de facturas electrónicas.

Archivo: `ui/electronic_invoice_detail/ElectronicInvoiceEditEmailScreen.kt`

#### Activación de Factura Electrónica
Proceso inicial para dar de alta el servicio en un contrato.

Archivo: `ui/electronic_invoice_detail/ElectronicInvoiceDetailFormScreen.kt`

#### Código de Verificación OTP
Pantalla para introducir el código recibido (SMS/Email). Incluye reenviación y contador de intentos. Estos mensajes se mandan a través de notificaciones.

Archivo: `ui/electronic_invoice_detail/ElectronicInvoiceOtpScreen.kt`
Componente: `ui/components/contract_selection/ResendSuccessBanner.kt`

#### Éxito de Factura Electrónica
Mensaje final que confirma la completitud del proceso.

Archivo: `ui/electronic_invoice_detail/SuccessScreen.kt`

---

## Funcionalidades Principales

### Gestión de Facturas
- Listado completo de facturas (Luz y Gas)
- Vista detallada de facturas con opciones de pago
- Descarga de PDF y compartición
- Filtros avanzados (fecha, importe, estado)
- Búsqueda por ID de factura

### Facturación Electrónica
- Activación/desactivación de servicio
- Validación mediante código OTP (notificación de app)
- Modificación de email de envío
- Estados persistentes

### Gestión de Usuario
- Perfil personalizable (nombre, email, teléfono)
- Contraseña segura (cifrada en local)
- Ocultamiento de importes

### Conectividad Inteligente
- Selector de origen de datos (API/Local)
- Caché automático con Room
- Sincronización offline-first
- Indicadores de estado de conexión

### Dashboard de Consumo
- Gráficos de consumo histórico
- Comparativas de períodos
- Consejos de ahorro energético

### Instrumentación
- Google Analytics para tracking
- Firebase Crashlytics para errores
- Remote Config dinámico
- Logs estructurados

---

## Instalación y Configuración

### Requisitos
- Android Studio Ladybug (2024.1) o superior
- JDK 17+
- Gradle 8.x
- Android SDK 33+

### Pasos de instalación

1. Clonar repositorio:
   git clone https://github.com/mpgea2004/IB2026MarPG.git

2. Configurar Firebase:
   - Descargar google-services.json desde Firebase Console
   - Colocar en carpeta app/

3. Sincronizar Gradle:
   ./gradlew clean
   ./gradlew build

4. Configurar Mockoon (opcional):
   - Descargar e instalar desde https://mockoon.com/
   - Importar: app/src/main/res/raw/mockoon_iberdrola.json
   - Ejecutar en https://localhost:3000

5. Ejecutar aplicación:
   ./gradlew assembleDebug
   O desde Android Studio: Run > Run 'app'

### Configuración de Versiones
- Target SDK: 34 (Android 14)
- Min SDK: 24 (Android 7)
- Compile SDK: 3

## Desarrollado por

MarPG - Prácticas de DAM
Programa: Viewnext Android
Año: 2026

---

## Estrategia de Testing

### Unit Tests (test/)
Validación de lógica en Use Cases, validadores y Mappers:

- GetInvoiceUseCaseTest: Obtención de facturas
- ValidateEmailUseCaseTest: Regex de validación
- ValidatePhoneUseCaseTest: Formato telefónico
- FormatUserPhoneUseCaseTest: Enmascaramiento
- VerifyUserPasswordUseCaseTest: Validación de contraseña
- InvoiceListViewModelTest: Estados y filtros
- FilterViewModelTest: Lógica de filtrado
- ProfileViewModelTest: Datos personales
- HomeViewModelTest: Inicialización
- ElectronicInvoiceViewModelTest: Flujo de activación
- InvoiceMapperTest: Conversión DTO > Domain
- ElectronicInvoiceMapperTest: Mappeo de fact. electrónica

### Android Tests (androidTest/)
Pruebas de integración CRUD en Room:

- InvoiceDaoTest: Operaciones de facturas
- UserDaoTest: Gestión de usuario
- ElectronicInvoiceDaoTest: Persistencia
- InvoiceDatabaseTest: Validación de singleton

