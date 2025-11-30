#!/bin/bash

# Script para limpiar y sincronizar el proyecto Android
# Este script resuelve problemas de cache y sincronización del IDE

echo "🧹 Limpiando proyecto MediAlert..."

# Detener daemons de Gradle
echo "1️⃣ Deteniendo Gradle Daemons..."
./gradlew --stop

# Limpiar build
echo "2️⃣ Limpiando build..."
./gradlew clean

# Eliminar caches de Gradle
echo "3️⃣ Eliminando caches de Gradle..."
rm -rf .gradle
rm -rf app/build
rm -rf build

# Limpiar caches del IDE (opcional, descomenta si usas Android Studio/IntelliJ)
echo "4️⃣ Limpiando caches del IDE..."
rm -rf .idea/caches
rm -rf .idea/libraries

# Reconstruir proyecto
echo "5️⃣ Reconstruyendo proyecto..."
./gradlew build

echo "✅ Limpieza completa finalizada!"
echo ""
echo "🔄 Si aún ves warnings en el IDE, ejecuta en Android Studio:"
echo "   File > Invalidate Caches / Restart"
echo "   File > Sync Project with Gradle Files"
