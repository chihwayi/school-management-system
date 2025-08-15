#!/bin/bash

echo "🚀 Starting School Management System in Development Mode..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Build and start all services in development mode
echo "📦 Building and starting services in development mode..."
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check service status
echo "🔍 Checking service status..."
docker-compose ps

echo "✅ School Management System is starting up in development mode!"
echo "🌐 Frontend: http://localhost:3000"
echo "🔧 Backend API: http://localhost:8080"
echo "🗄️  Database: localhost:3306"
echo "🐛 Debug Port: localhost:5005"
echo ""
echo "📋 Useful commands:"
echo "  - View logs: docker-compose logs -f"
echo "  - Stop services: docker-compose down"
echo "  - Restart services: docker-compose restart"
echo ""
echo "🔄 Hot reloading is enabled for both frontend and backend!"
