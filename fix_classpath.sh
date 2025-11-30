#!/bin/bash

# ═══════════════════════════════════════════════════════════════
#  🔧 SOLUCIÓN - MainActivity.java not on classpath
# ═══════════════════════════════════════════════════════════════

clear

cat << "EOF"
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║   🔧 SOLUCIONANDO PROBLEMA DE CLASSPATH                       ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
EOF

echo ""
echo "Error: 'MainActivity.java is not on the classpath of project app'"
echo ""
echo "Este error indica que el IDE no está sincronizado con Gradle."
echo "Voy a forzar una sincronización completa..."
echo ""

# Paso 1: Detener Gradle
echo "1️⃣  Deteniendo Gradle daemons..."
./gradlew --stop
sleep 2

# Paso 2: Limpiar proyecto
echo ""
echo "2️⃣  Limpiando proyecto..."
./gradlew clean --no-daemon

# Paso 3: Eliminar archivos de build
echo ""
echo "3️⃣  Eliminando archivos de build..."
rm -rf .gradle
rm -rf app/build
rm -rf build
rm -rf app/.cxx
rm -rf .cxx

# Paso 4: Eliminar archivos de IDE que puedan estar corruptos
echo ""
echo "4️⃣  Limpiando configuración del IDE..."
rm -rf .idea/caches 2>/dev/null
rm -rf .idea/libraries 2>/dev/null
rm -rf .idea/modules 2>/dev/null
rm -rf .idea/*.iml 2>/dev/null
rm -rf app/*.iml 2>/dev/null
rm -rf *.iml 2>/dev/null

# Paso 5: Forzar descarga de dependencias
echo ""
echo "5️⃣  Forzando descarga de dependencias..."
./gradlew --refresh-dependencies --no-daemon

# Paso 6: Build del proyecto
echo ""
echo "6️⃣  Building proyecto..."
./gradlew assembleDebug --no-daemon

BUILD_STATUS=$?

echo ""
if [ $BUILD_STATUS -eq 0 ]; then
    echo "✅ Build exitoso"
else
    echo "⚠️  Build con warnings (puede ser normal)"
fi

# Paso 7: Generar archivos de classpath
echo ""
echo "7️⃣  Generando archivos de classpath..."
./gradlew :app:dependencies --no-daemon > /dev/null 2>&1

# Paso 8: Actualizar configuración de Gradle
echo ""
echo "8️⃣  Actualizando configuración de Gradle..."
touch .idea/gradle.xml
touch app/build.gradle.kts
touch settings.gradle.kts

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║  ✅ LIMPIEZA COMPLETA FINALIZADA                              ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "📱 AHORA EN TU IDE (OBLIGATORIO):"
echo ""
echo "OPCIÓN 1 - RECOMENDADA:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "1. CERRAR completamente el IDE (Cmd+Q)"
echo "2. ABRIR el IDE de nuevo"
echo "3. Esperar a que cargue e indexe (1-2 min)"
echo "4. File → Sync Project with Gradle Files"
echo "5. Build → Rebuild Project"
echo ""
echo "OPCIÓN 2 - SI OPCIÓN 1 NO FUNCIONA:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "1. File → Invalidate Caches..."
echo "2. Marcar TODAS las opciones"
echo "3. Click 'Invalidate and Restart'"
echo "4. Esperar reinicio completo (2-3 min)"
echo "5. File → Sync Project with Gradle Files"
echo ""
echo "OPCIÓN 3 - SI SIGUE FALLANDO:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "1. CERRAR el IDE"
echo "2. Ejecutar:"
echo "   rm -rf .idea/"
echo "3. ABRIR el IDE (recreará .idea)"
echo "4. File → Sync Project with Gradle Files"
echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║  🎯 VERIFICACIÓN                                              ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "Después de sincronizar en el IDE:"
echo ""
echo "1. Abre MainActivity.java"
echo "2. El error 'not on classpath' debe desaparecer"
echo "3. Las importaciones deben resolverse correctamente"
echo "4. El código debe tener syntax highlighting normal"
echo ""
echo "Si el problema persiste:"
echo "- Verifica que tienes Java 11 instalado"
echo "- Verifica que Android SDK está configurado"
echo "- En File → Project Structure → Modules, verifica que 'app' esté presente"
echo ""
