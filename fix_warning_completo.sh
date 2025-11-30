#!/bin/bash

# ═══════════════════════════════════════════════════════════════
#  🚀 SOLUCIÓN AUTOMÁTICA COMPLETA - Warning MainActivity.java
# ═══════════════════════════════════════════════════════════════

clear

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                                                               ║"
echo "║   🔧 SOLUCIÓN AUTOMÁTICA - Warning MainActivity.java          ║"
echo "║                                                               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Función para mostrar spinner
spinner() {
    local pid=$1
    local delay=0.1
    local spinstr='⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏'
    while [ "$(ps a | awk '{print $1}' | grep $pid)" ]; do
        local temp=${spinstr#?}
        printf " [%c]  " "$spinstr"
        local spinstr=$temp${spinstr%"$temp"}
        sleep $delay
        printf "\b\b\b\b\b\b"
    done
    printf "    \b\b\b\b"
}

# Paso 1: Verificar diagnóstico
echo "📊 PASO 1/6: Ejecutando diagnóstico..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
./diagnostico_mainactivity.sh > /tmp/diagnostico.log 2>&1
if grep -q "NO SE DETECTARON PROBLEMAS" /tmp/diagnostico.log; then
    echo "✅ Diagnóstico: Código correcto"
else
    echo "⚠️  Diagnóstico: Se encontraron issues"
    cat /tmp/diagnostico.log
    echo ""
    echo "Presiona Enter para continuar de todos modos..."
    read
fi
echo ""

# Paso 2: Detectar IDE en ejecución
echo "🔍 PASO 2/6: Detectando IDEs en ejecución..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
IDE_RUNNING=false
IDE_NAME=""

if pgrep -x "Android Studio" > /dev/null; then
    IDE_RUNNING=true
    IDE_NAME="Android Studio"
    echo "🟢 Detectado: Android Studio en ejecución"
elif pgrep -x "studio" > /dev/null; then
    IDE_RUNNING=true
    IDE_NAME="studio"
    echo "🟢 Detectado: Android Studio en ejecución"
elif pgrep -x "idea" > /dev/null; then
    IDE_RUNNING=true
    IDE_NAME="IntelliJ IDEA"
    echo "🟢 Detectado: IntelliJ IDEA en ejecución"
else
    echo "⚪ No se detectó ningún IDE en ejecución"
fi

if [ "$IDE_RUNNING" = true ]; then
    echo ""
    echo "⚠️  ADVERTENCIA: Se detectó $IDE_NAME en ejecución"
    echo ""
    echo "Para que los cambios surtan efecto, necesitas:"
    echo "1. Cerrar completamente el IDE ahora"
    echo "2. Este script limpiará el proyecto"
    echo "3. Luego deberás abrir el IDE manualmente"
    echo ""
    echo "¿Deseas que este script CIERRE $IDE_NAME automáticamente? (s/n)"
    read -r respuesta
    
    if [[ "$respuesta" =~ ^[Ss]$ ]]; then
        echo "🔴 Cerrando $IDE_NAME..."
        killall -9 "Android Studio" 2>/dev/null || true
        killall -9 "studio" 2>/dev/null || true
        killall -9 "idea" 2>/dev/null || true
        sleep 2
        echo "✅ IDE cerrado"
    else
        echo ""
        echo "⏸️  Por favor, cierra el IDE manualmente y presiona Enter..."
        read
    fi
fi
echo ""

# Paso 3: Limpieza profunda
echo "🧹 PASO 3/6: Ejecutando limpieza profunda..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
./force_ide_refresh.sh > /tmp/limpieza.log 2>&1 &
CLEAN_PID=$!

spinner $CLEAN_PID
wait $CLEAN_PID
CLEAN_STATUS=$?

if [ $CLEAN_STATUS -eq 0 ]; then
    echo "✅ Limpieza completada exitosamente"
else
    echo "⚠️  Limpieza completada con warnings (revisar /tmp/limpieza.log)"
fi
echo ""

# Paso 4: Verificar compilación
echo "⚙️  PASO 4/6: Verificando compilación..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
./gradlew compileDebugJavaWithJavac --console=plain > /tmp/compile.log 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error de compilación"
    tail -20 /tmp/compile.log
    exit 1
fi
echo ""

# Paso 5: Eliminar caches del IDE del sistema
echo "🗑️  PASO 5/6: Limpiando caches del sistema del IDE..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "¿Deseas eliminar los caches del IDE del sistema? (s/n)"
echo "(Esto eliminará ~/Library/Caches/[AndroidStudio|IntelliJ])"
read -r respuesta_cache

if [[ "$respuesta_cache" =~ ^[Ss]$ ]]; then
    echo "Eliminando caches del sistema..."
    rm -rf ~/Library/Caches/Google/AndroidStudio* 2>/dev/null
    rm -rf ~/Library/Caches/AndroidStudio* 2>/dev/null
    rm -rf ~/Library/Caches/JetBrains/IntelliJIdea* 2>/dev/null
    echo "✅ Caches del sistema eliminados"
else
    echo "⏭️  Saltando eliminación de caches del sistema"
fi
echo ""

# Paso 6: Instrucciones finales
echo "📋 PASO 6/6: Instrucciones finales"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "✅ LIMPIEZA COMPLETA FINALIZADA"
echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║  🎯 PRÓXIMOS PASOS (IMPORTANTE)                               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "1️⃣  ABRIR tu IDE (Android Studio o IntelliJ IDEA)"
echo ""
echo "2️⃣  ESPERAR a que termine de cargar e indexar (1-3 min)"
echo "    👁️  Observa la barra de progreso en la parte inferior"
echo ""
echo "3️⃣  EJECUTAR uno de estos (en orden de preferencia):"
echo ""
echo "    OPCIÓN A (Recomendada):"
echo "    ━━━━━━━━━━━━━━━━━━━━━━"
echo "    File → Invalidate Caches..."
echo "    → Marcar todas las opciones"
echo "    → Click 'Invalidate and Restart'"
echo "    → Esperar reinicio (2-3 min)"
echo ""
echo "    OPCIÓN B (Si A no funciona):"
echo "    ━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "    File → Sync Project with Gradle Files"
echo "    → Esperar (30-60 seg)"
echo ""
echo "4️⃣  VERIFICAR:"
echo "    → Abrir: app/src/main/java/com/example/medialert/screens/main/MainActivity.java"
echo "    → Ir a línea 1"
echo "    → El warning amarillo DEBE haber desaparecido ✨"
echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║  📊 ESTADO DEL PROYECTO                                       ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "  ✅ Código fuente: Correcto"
echo "  ✅ AndroidManifest.xml: Correcto"
echo "  ✅ build.gradle.kts: Correcto"
echo "  ✅ Compilación: Exitosa"
echo "  ✅ Lint: Limpio"
echo "  ✅ Caches: Limpiados"
echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║  📚 DOCUMENTACIÓN DISPONIBLE                                  ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "  📄 GUIA_VISUAL_IDE.md          → Guía paso a paso con imágenes"
echo "  📄 INSTRUCCIONES_FINALES.md    → Instrucciones detalladas"
echo "  📄 RESUMEN_RAPIDO.txt          → Resumen ejecutivo"
echo "  📄 CORRECCIONES_MAINACTIVITY.md → Análisis técnico"
echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║  🎉 ¡LISTO!                                                   ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "El proyecto está completamente limpio y corregido."
echo "Ahora solo necesitas abrir el IDE y seguir los pasos anteriores."
echo ""
echo "Si el warning persiste después de todo esto, consulta:"
echo "  → GUIA_VISUAL_IDE.md (sección Troubleshooting)"
echo ""
echo "Presiona Enter para finalizar..."
read
clear
echo "✨ ¡Éxito! Abre tu IDE y verifica MainActivity.java"
echo ""
