#!/bin/bash

# echo "🧹 CLEAN"
# ./gradlew clean

echo "📦 Fast Build JAR (skip tests)"
./gradlew build -x test

if [ $? -ne 0 ]; then
  echo "❌ Gradle build failed. Exiting..."
  exit 1
fi

echo "🔻 DOWN (remove orphans)"
docker compose down --remove-orphans

echo "🔧 BUILD DOCKER"
docker compose build

echo "🚀 UP"
docker compose up -d

echo "📦 STATUS"
docker ps
