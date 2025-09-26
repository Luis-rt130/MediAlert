# MediAlert 📱💊

Una aplicación móvil para Android que ayuda a los usuarios a gestionar sus medicamentos y recordatorios de dosis de manera eficiente.

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Capturas de Pantalla](#capturas-de-pantalla)
- [Requisitos del Sistema](#requisitos-del-sistema)
- [Instalación](#instalación)
- [Uso de la Aplicación](#uso-de-la-aplicación)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Desarrollo](#desarrollo)
- [Contribución](#contribución)
- [Licencia](#licencia)

## 📱 Descripción

MediAlert es una aplicación móvil desarrollada en Android que permite a los usuarios:

- **Gestionar medicamentos**: Agregar, visualizar y organizar sus medicamentos
- **Configurar recordatorios**: Establecer horarios y frecuencias de medicación
- **Perfil de usuario**: Mantener información personal y gestionar sesión
- **Interfaz intuitiva**: Diseño moderno siguiendo Material Design 3

## ✨ Características

### 🔐 Autenticación
- Pantalla de inicio de sesión con validación de credenciales
- Gestión de perfil de usuario
- Funcionalidad de logout seguro

### 💊 Gestión de Medicamentos
- **Agregar medicamentos**: Formulario completo con:
  - Nombre del medicamento
  - Dosis (ej. 1 pastilla, 10 gotas)
  - Hora de inicio con selector de tiempo
  - Frecuencia de administración (cada 4, 6, 8, 12 o 24 horas)
- **Visualización**: Lista organizada de medicamentos con:
  - Información detallada de cada medicamento
  - Horarios de administración
  - Estado de próxima dosis

### 🎨 Interfaz de Usuario
- **Material Design 3**: Diseño moderno y consistente
- **Tema adaptable**: Soporte para modo claro y oscuro
- **Navegación intuitiva**: Menú de perfil y botón flotante para agregar medicamentos
- **Estados vacíos**: Mensajes informativos cuando no hay medicamentos

## 📸 Capturas de Pantalla

### Pantalla de Inicio de Sesión
- Logo de la aplicación
- Campos de email y contraseña
- Opciones de "¿Olvidaste tu contraseña?" y registro

### Pantalla Principal
- Lista de medicamentos con información detallada
- Botón flotante para agregar nuevos medicamentos
- Menú de perfil en la barra superior
- Estado vacío cuando no hay medicamentos

### Pantalla de Agregar Medicamento
- Formulario con campos para:
  - Nombre del medicamento
  - Dosis
  - Hora de inicio (selector de tiempo)
  - Frecuencia (dropdown con opciones predefinidas)
- Botones de Guardar y Cancelar

### Pantalla de Perfil
- Avatar del usuario
- Información personal (nombre y email)
- Botón de cerrar sesión

## 🔧 Requisitos del Sistema

- **Android**: Versión 7.0 (API 24) o superior
- **RAM**: Mínimo 2GB recomendado
- **Almacenamiento**: 50MB de espacio libre
- **Permisos**: Acceso a notificaciones para recordatorios

## 📦 Instalación

### Para Desarrolladores

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tu-usuario/MediAlert.git
   cd MediAlert
   ```

2. **Abrir en Android Studio**:
   - Abrir Android Studio
   - Seleccionar "Open an existing project"
   - Navegar a la carpeta del proyecto

3. **Sincronizar dependencias**:
   - Android Studio sincronizará automáticamente las dependencias
   - O ejecutar: `./gradlew build`

4. **Ejecutar la aplicación**:
   - Conectar dispositivo Android o iniciar emulador
   - Hacer clic en "Run" o presionar Shift+F10

### Para Usuarios Finales

1. **Descargar APK** (cuando esté disponible):
   - Descargar el archivo APK desde la sección de releases
   - Habilitar "Fuentes desconocidas" en configuración de Android
   - Instalar el APK

## 📱 Uso de la Aplicación

### Primer Uso

1. **Iniciar Sesión**:
   - Abrir la aplicación
   - Ingresar credenciales (actualmente simulado)
   - Tocar "Iniciar Sesión"

2. **Agregar Primer Medicamento**:
   - Tocar el botón "+" flotante
   - Completar el formulario:
     - Nombre: "Paracetamol 500mg"
     - Dosis: "1 pastilla"
     - Hora: Seleccionar horario deseado
     - Frecuencia: Elegir opción del dropdown
   - Tocar "Guardar"

### Uso Diario

1. **Ver Medicamentos**:
   - La pantalla principal muestra todos los medicamentos
   - Cada tarjeta muestra nombre, dosis y próxima hora

2. **Agregar Nuevos Medicamentos**:
   - Tocar el botón "+" flotante
   - Completar formulario y guardar

3. **Gestionar Perfil**:
   - Tocar el ícono de perfil en la barra superior
   - Ver información personal
   - Cerrar sesión si es necesario

## 🏗️ Arquitectura del Proyecto

```
app/
├── src/main/
│   ├── java/com/example/medialert/
│   │   └── screens/
│   │       ├── login/           # Autenticación
│   │       ├── main/            # Pantalla principal
│   │       ├── addmedicine/     # Agregar medicamentos
│   │       └── profile/         # Perfil de usuario
│   ├── res/
│   │   ├── layout/              # Diseños XML
│   │   ├── values/              # Strings, colores, temas
│   │   ├── menu/                # Menús de navegación
│   │   └── drawable/            # Iconos y recursos gráficos
│   └── AndroidManifest.xml      # Configuración de la app
```

### Estructura de Pantallas

- **LoginActivity**: Manejo de autenticación
- **MainActivity**: Lista principal de medicamentos
- **AddMedicineActivity**: Formulario para agregar medicamentos
- **ProfileActivity**: Gestión de perfil y logout

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Java 11
- **Framework**: Android SDK
- **UI**: Material Design 3
- **Arquitectura**: Activity-based (MVC)
- **Dependencias**:
  - AndroidX AppCompat
  - Material Components
  - ConstraintLayout
  - Activity

### Versiones

- **Compile SDK**: 36
- **Target SDK**: 36
- **Min SDK**: 24
- **Gradle**: 8.12.3

## 🚀 Desarrollo

### Configuración del Entorno

1. **Android Studio**: Arctic Fox o superior
2. **JDK**: 11 o superior
3. **Android SDK**: API 24-36

### Comandos Útiles

```bash
# Limpiar proyecto
./gradlew clean

# Compilar en modo debug
./gradlew assembleDebug

# Ejecutar pruebas
./gradlew test

# Generar APK de release
./gradlew assembleRelease
```

### Estructura de Código

- **Actividades**: Una por pantalla principal
- **Layouts**: Diseños XML con ConstraintLayout
- **Recursos**: Strings, colores y temas centralizados
- **Navegación**: Intents para comunicación entre pantallas

## 🤝 Contribución

### Cómo Contribuir

1. **Fork** el proyecto
2. **Crear** una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. **Push** a la rama (`git push origin feature/AmazingFeature`)
5. **Abrir** un Pull Request

### Estándares de Código

- Seguir convenciones de Java/Android
- Comentar código complejo
- Mantener consistencia en el estilo
- Probar cambios antes de commit

### Roadmap

- [ ] Implementar base de datos local (Room)
- [ ] Agregar notificaciones push
- [ ] Sistema de autenticación real
- [ ] Historial de medicamentos tomados
- [ ] Exportar/importar datos
- [ ] Múltiples idiomas
- [ ] Modo offline

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto

- **Desarrollador**: [Tu Nombre]
- **Email**: tu-email@ejemplo.com
- **Proyecto**: [https://github.com/tu-usuario/MediAlert](https://github.com/tu-usuario/MediAlert)

## 🙏 Agradecimientos

- Material Design por Google
- AndroidX por el soporte de compatibilidad
- Comunidad de Android por las mejores prácticas

---

**MediAlert** - Mantén tu salud bajo control 💊✨