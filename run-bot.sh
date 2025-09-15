#!/bin/bash

echo "🚀 Starting Arogya-Sakhi Bot..."

# Set environment variables
export BOT_TOKEN=${BOT_TOKEN}
export BOT_USERNAME=${BOT_USERNAME}
export GEMINI_API_KEY=${GEMINI_API_KEY}
export MONGO_URI=${MONGO_URI}

echo "✅ Environment variables set"

# Clean and compile
echo "🔧 Cleaning and compiling..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful"
    echo "🚀 Starting the bot..."
    mvn spring-boot:run
else
    echo "❌ Compilation failed"
    exit 1
fi
