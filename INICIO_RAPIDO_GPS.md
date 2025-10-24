# 🚀 Inicio Rápido - Funcionalidad GPS

## ⚡ Configuración en 3 Pasos

### Paso 1: Obtener Google Maps API Key

1. Ve a https://console.cloud.google.com/
2. Crea un proyecto nuevo o selecciona uno existente
3. Habilita **"Maps SDK for Android"** en la librería de APIs
4. Ve a **Credentials** → **Create Credentials** → **API Key**
5. Copia tu API Key

### Paso 2: Configurar la API Key

**Opción A - Rápida (para testing):**
1. Abre `app/src/main/AndroidManifest.xml`
2. Busca `YOUR_API_KEY_HERE`
3. Reemplázalo con tu API Key

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyAbCdEfGhIjKlMnOpQrStUvWxYz123456" />
```

**Opción B - Segura (para producción):**
1. Abre `local.properties` (en la raíz del proyecto)
2. Agrega: `MAPS_API_KEY=tu_api_key_aqui`
3. Modifica `app/build.gradle.kts` según instrucciones en CONFIGURACION_GPS.md

### Paso 3: Ejecutar la App

```bash
# Sincronizar dependencias
./gradlew build

# Ejecutar en dispositivo/emulador
./gradlew installDebug
```

## 📱 Usar la Funcionalidad

1. **Abrir la app**
2. **Tocar el ícono de mapa** (📍) en la barra superior
3. **Conceder permisos** de ubicación cuando se solicite
4. **Ver tu ubicación** en tiempo real en el mapa

## ✅ Verificación Rápida

### ¿Todo funciona?
- [ ] El mapa se muestra correctamente
- [ ] Aparece un marcador en tu ubicación
- [ ] Las coordenadas se actualizan en la tarjeta inferior
- [ ] La precisión se muestra en metros

### ¿Problemas?

**El mapa no se muestra:**
- ✅ Verifica que la API Key esté correctamente configurada
- ✅ Confirma que "Maps SDK for Android" esté habilitada
- ✅ Revisa los logs en Android Studio (Logcat)

**No obtiene ubicación:**
- ✅ Concede los permisos de ubicación
- ✅ Habilita el GPS en el dispositivo
- ✅ Prueba en exteriores para mejor señal

**Permisos no se solicitan:**
- ✅ Desinstala y reinstala la app
- ✅ Verifica permisos en Settings → Apps → MediAlert

## 🎨 Características Visuales

- **Mapa interactivo**: Zoom y pan con gestos
- **Marcador**: Indica tu posición actual
- **Información**: Latitud, longitud y precisión
- **Estados claros**: Loading, sin permisos, errores
- **Diseño minimalista**: Alineado con MediAlert

## 🔒 Seguridad

**⚠️ IMPORTANTE**: No subas tu API Key a repositorios públicos

Si ya lo hiciste:
1. Regenera la API Key en Google Cloud Console
2. Invalida la anterior
3. Usa el método seguro (local.properties)

## 📚 Más Información

- **Guía completa**: Ver [CONFIGURACION_GPS.md](CONFIGURACION_GPS.md)
- **Resumen técnico**: Ver [IMPLEMENTACION_GPS_RESUMEN.md](IMPLEMENTACION_GPS_RESUMEN.md)
- **README principal**: Ver [README.md](README.md)

## 💡 Tips

1. **Testing en emulador**: Puedes simular ubicaciones en Android Studio
2. **Mejor precisión**: Usa en exteriores con GPS activado
3. **Ahorro de batería**: La app detiene updates cuando está en background
4. **Actualización**: El mapa se actualiza cada 5 segundos

## 🆘 Soporte

Si tienes problemas:

1. **Revisa los logs**: Android Studio → Logcat
2. **Verifica permisos**: Settings → Apps → MediAlert → Permissions
3. **Confirma GPS**: Settings → Location
4. **API Key válida**: Google Cloud Console → Credentials

---

¡Listo! Tu app ahora tiene funcionalidad de GPS en tiempo real 🎉

**Tiempo estimado de configuración**: 5-10 minutos  
**Dificultad**: Fácil ⭐
