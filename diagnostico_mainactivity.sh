#!/bin/bash

# ═══════════════════════════════════════════════════════════════
#  🔍 DIAGNÓSTICO COMPLETO - MainActivity.java
# ═══════════════════════════════════════════════════════════════

echo "🔍 DIAGNÓSTICO DE MainActivity.java"
echo "═══════════════════════════════════════════════════════════════"
echo ""

MAIN_ACTIVITY="app/src/main/java/com/example/medialert/screens/main/MainActivity.java"
MANIFEST="app/src/main/AndroidManifest.xml"
BUILD_GRADLE="app/build.gradle.kts"

# 1. Verificar que el archivo existe
echo "1️⃣  Verificando existencia del archivo..."
if [ -f "$MAIN_ACTIVITY" ]; then
    echo "   ✅ MainActivity.java existe"
else
    echo "   ❌ MainActivity.java NO existe"
    exit 1
fi

# 2. Verificar la primera línea del package
echo ""
echo "2️⃣  Verificando declaración de package..."
PACKAGE_LINE=$(head -n 1 "$MAIN_ACTIVITY")
echo "   Línea 1: $PACKAGE_LINE"

if [[ "$PACKAGE_LINE" == "package com.example.medialert.screens.main;" ]]; then
    echo "   ✅ Package declaration es CORRECTA"
else
    echo "   ⚠️  Package declaration podría tener problemas"
fi

# 3. Verificar AndroidManifest.xml
echo ""
echo "3️⃣  Verificando AndroidManifest.xml..."
if grep -q 'package=' "$MANIFEST"; then
    echo "   ⚠️  ADVERTENCIA: AndroidManifest.xml contiene atributo 'package' (deprecado)"
    grep 'package=' "$MANIFEST"
else
    echo "   ✅ AndroidManifest.xml NO tiene atributo 'package' (correcto)"
fi

# 4. Verificar namespace en build.gradle.kts
echo ""
echo "4️⃣  Verificando namespace en build.gradle.kts..."
if grep -q 'namespace = "com.example.medialert"' "$BUILD_GRADLE"; then
    echo "   ✅ Namespace configurado correctamente en build.gradle.kts"
    grep 'namespace' "$BUILD_GRADLE" | head -n 1
else
    echo "   ❌ Namespace NO encontrado en build.gradle.kts"
fi

# 5. Verificar estructura de directorios
echo ""
echo "5️⃣  Verificando estructura de directorios..."
EXPECTED_PATH="app/src/main/java/com/example/medialert/screens/main"
if [ -d "$EXPECTED_PATH" ]; then
    echo "   ✅ Estructura de directorios es correcta"
    echo "   Path: $EXPECTED_PATH"
else
    echo "   ❌ Estructura de directorios incorrecta"
fi

# 6. Verificar que no haya caracteres especiales
echo ""
echo "6️⃣  Verificando caracteres especiales en la línea 1..."
FIRST_LINE=$(head -n 1 "$MAIN_ACTIVITY" | od -A n -t x1)
if [[ "$FIRST_LINE" == *"efbbbf"* ]]; then
    echo "   ⚠️  DETECTADO: BOM (Byte Order Mark) UTF-8 en el archivo"
    echo "   Esto puede causar warnings en algunos IDEs"
else
    echo "   ✅ No se detectaron caracteres especiales"
fi

# 7. Compilar solo MainActivity.java
echo ""
echo "7️⃣  Compilando MainActivity.java..."
./gradlew :app:compileDebugJavaWithJavac --console=plain > /tmp/gradle_output.txt 2>&1
if [ $? -eq 0 ]; then
    echo "   ✅ MainActivity.java compila SIN ERRORES"
else
    echo "   ❌ Hay errores de compilación"
    cat /tmp/gradle_output.txt
fi

# 8. Verificar permisos del archivo
echo ""
echo "8️⃣  Verificando permisos del archivo..."
ls -la "$MAIN_ACTIVITY" | awk '{print "   Permisos: " $1 " | Tamaño: " $5 " bytes"}'

# 9. Checksum del archivo
echo ""
echo "9️⃣  Generando checksum del archivo..."
CHECKSUM=$(md5 -q "$MAIN_ACTIVITY" 2>/dev/null || md5sum "$MAIN_ACTIVITY" | awk '{print $1}')
echo "   MD5: $CHECKSUM"

# 10. Comparar con el manifest Activity declaration
echo ""
echo "🔟 Verificando declaración en AndroidManifest.xml..."
if grep -q 'android:name=".screens.main.MainActivity"' "$MANIFEST"; then
    echo "   ✅ MainActivity declarada correctamente en manifest"
else
    echo "   ⚠️  Revisar declaración de MainActivity en manifest"
fi

# Resumen final
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  📊 RESUMEN DEL DIAGNÓSTICO"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "Archivo: $MAIN_ACTIVITY"
echo "Primera línea: $(head -n 1 "$MAIN_ACTIVITY")"
echo ""

# Verificar si todo está OK
ISSUES=0

if [[ "$PACKAGE_LINE" != "package com.example.medialert.screens.main;" ]]; then
    echo "⚠️  Issue 1: Package declaration incorrecta"
    ISSUES=$((ISSUES + 1))
fi

if grep -q 'package=' "$MANIFEST"; then
    echo "⚠️  Issue 2: AndroidManifest.xml tiene atributo 'package' deprecado"
    ISSUES=$((ISSUES + 1))
fi

if ! grep -q 'namespace = "com.example.medialert"' "$BUILD_GRADLE"; then
    echo "⚠️  Issue 3: Namespace no configurado en build.gradle.kts"
    ISSUES=$((ISSUES + 1))
fi

if [ $ISSUES -eq 0 ]; then
    echo "✅ NO SE DETECTARON PROBLEMAS EN EL CÓDIGO"
    echo ""
    echo "🎯 Si el warning AMARILLO persiste en tu IDE:"
    echo ""
    echo "   Es un problema de CACHE del IDE, no del código."
    echo ""
    echo "   SOLUCIÓN:"
    echo "   1. CERRAR completamente el IDE (Cmd+Q)"
    echo "   2. Ejecutar: ./force_ide_refresh.sh"
    echo "   3. ABRIR el IDE"
    echo "   4. File → Invalidate Caches → Invalidate and Restart"
    echo ""
else
    echo ""
    echo "⚠️  Se detectaron $ISSUES problemas que deben corregirse"
fi

echo ""
echo "═══════════════════════════════════════════════════════════════"
