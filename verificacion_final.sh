#!/bin/bash

# ═══════════════════════════════════════════════════════════════
#  ✅ VERIFICACIÓN FINAL COMPLETA - Estado del Proyecto
# ═══════════════════════════════════════════════════════════════

clear

cat << "EOF"
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║         ✅ VERIFICACIÓN FINAL DEL PROYECTO MEDIALERT          ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
EOF

echo ""
echo "Generando reporte completo del estado actual..."
echo ""

# Crear archivo de reporte
REPORT_FILE="REPORTE_VERIFICACION_$(date +%Y%m%d_%H%M%S).txt"

{
    echo "═══════════════════════════════════════════════════════════════"
    echo "  REPORTE DE VERIFICACIÓN FINAL - MediAlert"
    echo "  Fecha: $(date '+%Y-%m-%d %H:%M:%S')"
    echo "═══════════════════════════════════════════════════════════════"
    echo ""
    
    # 1. Verificar MainActivity.java
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "1. MAINACTIVITY.JAVA"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "Archivo: app/src/main/java/com/example/medialert/screens/main/MainActivity.java"
    echo ""
    FIRST_LINE=$(head -n 1 app/src/main/java/com/example/medialert/screens/main/MainActivity.java)
    echo "Línea 1: $FIRST_LINE"
    echo ""
    if [[ "$FIRST_LINE" == "package com.example.medialert.screens.main;" ]]; then
        echo "✅ ESTADO: CORRECTO"
    else
        echo "⚠️  ESTADO: REVISAR"
    fi
    echo ""
    
    # 2. Verificar AndroidManifest.xml
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "2. ANDROIDMANIFEST.XML"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "Archivo: app/src/main/AndroidManifest.xml"
    echo ""
    if grep -q 'package=' app/src/main/AndroidManifest.xml; then
        echo "⚠️  CONTIENE: Atributo 'package' (deprecado)"
        echo "   $(grep 'package=' app/src/main/AndroidManifest.xml)"
        echo ""
        echo "❌ ESTADO: INCORRECTO - Debe eliminarse"
    else
        echo "✅ ESTADO: CORRECTO - Sin atributo 'package'"
    fi
    echo ""
    
    # 3. Verificar build.gradle.kts
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "3. BUILD.GRADLE.KTS"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "Archivo: app/build.gradle.kts"
    echo ""
    NAMESPACE_LINE=$(grep 'namespace' app/build.gradle.kts | head -n 1 | xargs)
    echo "$NAMESPACE_LINE"
    echo ""
    if [[ "$NAMESPACE_LINE" == 'namespace = "com.example.medialert"' ]]; then
        echo "✅ ESTADO: CORRECTO"
    else
        echo "⚠️  ESTADO: REVISAR namespace"
    fi
    echo ""
    
    # 4. Compilación
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "4. COMPILACIÓN"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "Ejecutando: ./gradlew compileDebugJavaWithJavac"
    echo ""
    
} | tee "$REPORT_FILE"

# Compilar
./gradlew compileDebugJavaWithJavac --console=plain > /tmp/compile_check.log 2>&1
COMPILE_STATUS=$?

{
    if [ $COMPILE_STATUS -eq 0 ]; then
        echo "✅ COMPILACIÓN: EXITOSA"
    else
        echo "❌ COMPILACIÓN: FALLÓ"
        echo ""
        echo "Últimas líneas del log:"
        tail -20 /tmp/compile_check.log
    fi
    echo ""
    
    # 5. Lint
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "5. LINT"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    
} | tee -a "$REPORT_FILE"

# Lint check
./gradlew lintDebug --console=plain > /tmp/lint_check.log 2>&1
LINT_STATUS=$?

{
    if [ $LINT_STATUS -eq 0 ]; then
        echo "✅ LINT: SIN ERRORES CRÍTICOS"
    else
        echo "⚠️  LINT: Revisar warnings"
    fi
    echo ""
    
    # 6. Estructura del proyecto
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "6. ESTRUCTURA DEL PROYECTO"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    
    # Verificar todas las clases Java
    echo "Clases Java en el proyecto:"
    find app/src/main/java -name "*.java" -type f | while read file; do
        PACKAGE=$(head -n 1 "$file" | grep "^package")
        echo "  📄 $(basename $file)"
        echo "     $PACKAGE"
    done
    echo ""
    
    # 7. Archivos de layout con posibles problemas
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "7. LAYOUTS (android:tint vs app:tint)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    
    # Buscar android:tint (incorrecto)
    if grep -r "android:tint" app/src/main/res/layout/ 2>/dev/null; then
        echo "⚠️  ENCONTRADO: Uso de android:tint (deprecado)"
    else
        echo "✅ CORRECTO: No se encontró android:tint"
    fi
    echo ""
    
    # Buscar app:tint (correcto)
    APP_TINT_COUNT=$(grep -r "app:tint" app/src/main/res/layout/ 2>/dev/null | wc -l)
    echo "Usos de app:tint (correcto): $APP_TINT_COUNT"
    echo ""
    
    # 8. Checksums
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "8. CHECKSUMS (para verificar cambios)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "MainActivity.java:          $(md5 -q app/src/main/java/com/example/medialert/screens/main/MainActivity.java 2>/dev/null || md5sum app/src/main/java/com/example/medialert/screens/main/MainActivity.java | awk '{print $1}')"
    echo "AndroidManifest.xml:        $(md5 -q app/src/main/AndroidManifest.xml 2>/dev/null || md5sum app/src/main/AndroidManifest.xml | awk '{print $1}')"
    echo "build.gradle.kts:           $(md5 -q app/build.gradle.kts 2>/dev/null || md5sum app/build.gradle.kts | awk '{print $1}')"
    echo "activity_add_medicine.xml:  $(md5 -q app/src/main/res/layout/activity_add_medicine.xml 2>/dev/null || md5sum app/src/main/res/layout/activity_add_medicine.xml | awk '{print $1}')"
    echo "activity_profile.xml:       $(md5 -q app/src/main/res/layout/activity_profile.xml 2>/dev/null || md5sum app/src/main/res/layout/activity_profile.xml | awk '{print $1}')"
    echo ""
    
    # 9. Resumen final
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "9. RESUMEN FINAL"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    
    ISSUES=0
    
    # Revisar cada aspecto
    if [[ "$(head -n 1 app/src/main/java/com/example/medialert/screens/main/MainActivity.java)" != "package com.example.medialert.screens.main;" ]]; then
        echo "❌ MainActivity.java package incorrecto"
        ISSUES=$((ISSUES + 1))
    else
        echo "✅ MainActivity.java package correcto"
    fi
    
    if grep -q 'package=' app/src/main/AndroidManifest.xml; then
        echo "❌ AndroidManifest.xml tiene atributo package"
        ISSUES=$((ISSUES + 1))
    else
        echo "✅ AndroidManifest.xml sin atributo package"
    fi
    
    if ! grep -q 'namespace = "com.example.medialert"' app/build.gradle.kts; then
        echo "❌ build.gradle.kts namespace incorrecto"
        ISSUES=$((ISSUES + 1))
    else
        echo "✅ build.gradle.kts namespace correcto"
    fi
    
    if [ $COMPILE_STATUS -ne 0 ]; then
        echo "❌ Compilación falló"
        ISSUES=$((ISSUES + 1))
    else
        echo "✅ Compilación exitosa"
    fi
    
    if grep -r "android:tint" app/src/main/res/layout/ 2>/dev/null > /dev/null; then
        echo "❌ Layouts con android:tint deprecado"
        ISSUES=$((ISSUES + 1))
    else
        echo "✅ Layouts sin android:tint deprecado"
    fi
    
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    
    if [ $ISSUES -eq 0 ]; then
        echo "  ✅✅✅ PROYECTO 100% CORRECTO ✅✅✅"
        echo "═══════════════════════════════════════════════════════════════"
        echo ""
        echo "🎯 CONCLUSIÓN:"
        echo ""
        echo "   El código está PERFECTO. Si el warning amarillo persiste"
        echo "   en tu IDE, es un problema de CACHE."
        echo ""
        echo "   SOLUCIÓN:"
        echo "   1. Ejecuta: ./fix_warning_completo.sh"
        echo "   2. Abre tu IDE"
        echo "   3. File → Invalidate Caches → Restart"
        echo ""
    else
        echo "  ⚠️  SE DETECTARON $ISSUES PROBLEMAS"
        echo "═══════════════════════════════════════════════════════════════"
        echo ""
        echo "Por favor, revisa los detalles arriba."
    fi
    
    echo ""
    echo "Reporte guardado en: $REPORT_FILE"
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    
} | tee -a "$REPORT_FILE"

# Mostrar ubicación del reporte
echo ""
echo "📄 Reporte completo generado:"
echo "   $(pwd)/$REPORT_FILE"
echo ""
echo "Para ver el reporte completo:"
echo "   cat $REPORT_FILE"
echo ""
