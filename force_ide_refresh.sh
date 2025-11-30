#!/bin/bash

# ═══════════════════════════════════════════════════════════════
#  🔥 SCRIPT DE LIMPIEZA PROFUNDA - FORZAR REFRESCO DEL IDE
# ═══════════════════════════════════════════════════════════════

echo "🔥 INICIANDO LIMPIEZA PROFUNDA Y FORZADA..."
echo ""

# 1. Detener todos los procesos de Gradle
echo "1️⃣  Deteniendo Gradle Daemons..."
./gradlew --stop
killall -9 java 2>/dev/null || true

# 2. Limpiar completamente Gradle
echo "2️⃣  Limpiando Gradle completamente..."
./gradlew clean --no-daemon

# 3. Eliminar TODOS los directorios de build
echo "3️⃣  Eliminando directorios de build..."
rm -rf .gradle
rm -rf app/build
rm -rf build
rm -rf app/.cxx
rm -rf .cxx

# 4. Eliminar caches del IDE (sin tocar configuración)
echo "4️⃣  Limpiando caches del IDE..."
rm -rf .idea/caches 2>/dev/null || true
rm -rf .idea/libraries 2>/dev/null || true
rm -rf .idea/shelf 2>/dev/null || true
rm -rf .idea/workspace.xml 2>/dev/null || true
rm -rf .idea/tasks.xml 2>/dev/null || true
rm -rf .idea/usage.statistics.xml 2>/dev/null || true
rm -rf .idea/dictionaries 2>/dev/null || true
rm -rf .idea/sonarlint 2>/dev/null || true

# 5. Actualizar timestamp del proyecto
echo "5️⃣  Actualizando timestamp del proyecto..."
touch .idea/misc.xml
touch .idea/gradle.xml
touch app/build.gradle.kts
touch settings.gradle.kts

# 6. Limpiar archivos .class y .dex
echo "6️⃣  Limpiando archivos compilados..."
find . -type f -name "*.class" -delete 2>/dev/null || true
find . -type f -name "*.dex" -delete 2>/dev/null || true

# 7. Reconstruir proyecto desde cero
echo "7️⃣  Reconstruyendo proyecto desde cero..."
./gradlew clean build --refresh-dependencies --no-daemon

# 8. Verificación final
echo ""
echo "8️⃣  Verificación final..."
./gradlew compileDebugJavaWithJavac --console=plain

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  ✅ LIMPIEZA PROFUNDA COMPLETADA"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "📱 AHORA EN TU IDE (ANDROID STUDIO / INTELLIJ):"
echo ""
echo "   OPCIÓN 1 (Recomendada):"
echo "   ----------------------"
echo "   1. CERRAR completamente el IDE (Cmd+Q o File → Exit)"
echo "   2. ABRIR el IDE de nuevo"
echo "   3. ESPERAR a que cargue e indexe (1-2 min)"
echo "   4. File → Sync Project with Gradle Files"
echo ""
echo "   OPCIÓN 2 (Si OPCIÓN 1 no funciona):"
echo "   -----------------------------------"
echo "   1. File → Invalidate Caches..."
echo "   2. Marcar TODAS las opciones"
echo "   3. Click 'Invalidate and Restart'"
echo "   4. Esperar reinicio completo (2-3 min)"
echo ""
echo "   OPCIÓN 3 (Máxima potencia):"
echo "   ---------------------------"
echo "   1. CERRAR el IDE"
echo "   2. Eliminar caches del sistema:"
echo "      rm -rf ~/Library/Caches/Google/AndroidStudio*"
echo "      rm -rf ~/Library/Caches/AndroidStudio*"
echo "      rm -rf ~/Library/Caches/JetBrains/IntelliJIdea*"
echo "   3. ABRIR el IDE de nuevo"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "🎯 El warning amarillo DEBE desaparecer después de estos pasos."
echo "   Si persiste, es posible que sea un bug del IDE."
echo ""
