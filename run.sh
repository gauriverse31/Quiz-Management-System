#!/bin/bash
# ─────────────────────────────────────────────────────────────
# QuizPro — Build & Run Script
# Usage: ./run.sh
# ─────────────────────────────────────────────────────────────

cd "$(dirname "$0")"

echo "🔨 Compiling QuizPro..."
mkdir -p out data

# Compile all Java files
find src -name "*.java" | xargs javac -d out -sourcepath src

if [ $? -ne 0 ]; then
  echo "❌ Compilation failed!"
  exit 1
fi

echo "✅ Compilation successful!"
echo "🚀 Launching QuizPro..."
java -cp out Main
