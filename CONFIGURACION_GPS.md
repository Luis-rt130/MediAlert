# Configuración de GPS y Google Maps 📍

## Descripción

Se ha implementado una funcionalidad robusta de GPS que permite visualizar la ubicación del dispositivo en tiempo real con las siguientes características:

### ✨ Características Implementadas

- **Visualización en tiempo real**: Mapa interactivo con actualización cada 5 segundos
- **Información detallada**: Muestra latitud, longitud y precisión de la ubicación
- **Manejo robusto de permisos**: Solicitud inteligente y guiada de permisos de ubicación
- **Manejo de errores**: Detecta y notifica cuando GPS está deshabilitado o hay problemas
- **Diseño minimalista**: Interfaz consistente con el resto de la aplicación
- **Estados claros**: Muestra estados de carga, permisos requeridos y errores

### 🎨 Diseño

La implementación sigue el diseño minimalista de MediAlert con:
- Tarjetas Material Design con bordes redondeados
- Colores consistentes con la paleta de la app
- Iconos claros y descriptivos
- Transiciones suaves entre estados
- Layout responsive y adaptable

## 🔑 Configuración de Google Maps API Key

Para que el mapa funcione correctamente, necesitas obtener una API Key de Google Maps. Sigue estos pasos:

### Paso 1: Crear/Acceder a un Proyecto en Google Cloud Console

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Inicia sesión con tu cuenta de Google
3. Crea un nuevo proyecto o selecciona uno existente

### Paso 2: Habilitar las APIs necesarias

1. En el menú lateral, ve a **APIs & Services** > **Library**
2. Busca y habilita las siguientes APIs:
   - **Maps SDK for Android**
   - **Geocoding API** (opcional, para funcionalidades futuras)

### Paso 3: Crear una API Key

1. Ve a **APIs & Services** > **Credentials**
2. Click en **Create Credentials** > **API Key**
3. Se generará una API Key
4. (Recomendado) Click en **Restrict Key** para configurar restricciones:
   - **Application restrictions**: Android apps
   - Agrega el package name: `com.example.medialert`
   - Agrega la huella digital SHA-1 de tu certificado de firma

### Paso 4: Obtener SHA-1 Fingerprint

Para desarrollo, ejecuta en la terminal:

```bash
# En el directorio raíz del proyecto
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copia el valor de **SHA1** y agrégalo en la configuración de la API Key.

### Paso 5: Agregar la API Key al Proyecto

Edita el archivo `app/src/main/AndroidManifest.xml` y reemplaza `YOUR_API_KEY_HERE` con tu API Key:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
```

**IMPORTANTE**: Nunca subas tu API Key a repositorios públicos. Considera usar:
- Archivo `local.properties` (ignorado por git)
- Variables de entorno
- Android keystore

## 🔒 Seguridad de la API Key

### Método Recomendado: Usar local.properties

1. **Agrega la API Key a `local.properties`** (este archivo ya está en .gitignore):
```properties
MAPS_API_KEY=tu_api_key_aqui
```

2. **Modifica `app/build.gradle.kts`** para leer la key:
```kotlin
android {
    // ...
    
    defaultConfig {
        // ...
        
        // Leer API Key desde local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY", "")
    }
}
```

3. **Actualiza `AndroidManifest.xml`**:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

## 📱 Uso de la Funcionalidad

### Acceso

1. Abre la aplicación MediAlert
2. En la pantalla principal, toca el ícono de mapa (📍) en la barra superior
3. Concede los permisos de ubicación cuando se soliciten

### Primera Vez

La app solicitará permisos de ubicación:
- **ACCESS_FINE_LOCATION**: Para ubicación precisa via GPS
- **ACCESS_COARSE_LOCATION**: Para ubicación aproximada via red

### Estados de la Pantalla

1. **Cargando**: Mientras se obtiene la primera ubicación
2. **Mapa con Ubicación**: Muestra el mapa con un marcador en tu posición
3. **Información de Ubicación**: Card con latitud, longitud y precisión
4. **Sin Permisos**: Solicita conceder permisos con botón de acción
5. **GPS Deshabilitado**: Ofrece ir a configuración para habilitar GPS
6. **Error**: Muestra mensaje de error si algo falla

## 🛠️ Componentes Técnicos

### Dependencias Agregadas

```kotlin
implementation("com.google.android.gms:play-services-location:21.3.0")
implementation("com.google.android.gms:play-services-maps:19.0.0")
```

### Permisos Agregados

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### Archivos Creados/Modificados

**Nuevos Archivos:**
- `app/src/main/java/com/example/medialert/screens/location/LocationActivity.java`
- `app/src/main/res/layout/activity_location.xml`

**Archivos Modificados:**
- `gradle/libs.versions.toml` - Versiones de dependencias
- `app/build.gradle.kts` - Dependencias de Google Play Services
- `app/src/main/AndroidManifest.xml` - Permisos y API Key
- `app/src/main/res/values/strings.xml` - Strings de ubicación
- `app/src/main/res/menu/menu_main.xml` - Ítem de menú
- `app/src/main/java/com/example/medialert/screens/main/MainActivity.java` - Handler del menú

## 🔧 Resolución de Problemas

### El mapa no se muestra

1. Verifica que la API Key esté correctamente configurada
2. Confirma que **Maps SDK for Android** esté habilitada en Google Cloud Console
3. Revisa que el package name y SHA-1 coincidan en las restricciones

### No se obtiene ubicación

1. Verifica que los permisos estén concedidos en la configuración del dispositivo
2. Confirma que el GPS esté habilitado
3. Intenta en exteriores o con buena señal GPS
4. Revisa los logs de Android Studio para errores específicos

### Error de permisos

1. Desinstala y reinstala la app
2. Ve a Configuración > Apps > MediAlert > Permisos
3. Asegúrate de conceder los permisos de ubicación

## 📊 Configuración de Actualizaciones

Las actualizaciones de ubicación están configuradas como:

- **Intervalo de actualización**: 5 segundos
- **Intervalo mínimo**: 2 segundos
- **Prioridad**: Alta precisión (GPS)

Puedes modificar estos valores en `LocationActivity.java`:

```java
private static final long UPDATE_INTERVAL = 5000; // 5 segundos
private static final long FASTEST_INTERVAL = 2000; // 2 segundos
```

## 🚀 Mejoras Futuras

Posibles mejoras a implementar:

- [ ] Historial de ubicaciones visitadas
- [ ] Geofencing para recordatorios basados en ubicación
- [ ] Compartir ubicación con contactos de emergencia
- [ ] Guardar ubicaciones de farmacias cercanas
- [ ] Rutas a ubicaciones guardadas
- [ ] Modo de ahorro de batería con actualizaciones menos frecuentes

## 📞 Soporte

Si encuentras algún problema con la funcionalidad de GPS:

1. Verifica los logs en Android Studio
2. Confirma que todos los pasos de configuración se completaron
3. Revisa la documentación oficial de [Google Maps Platform](https://developers.google.com/maps/documentation/android-sdk)

---

**MediAlert** - Tu salud bajo control, tu ubicación siempre segura 💊📍
