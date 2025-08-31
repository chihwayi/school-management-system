#!/bin/bash

echo "🚀 Starting School Management System in DEVELOPMENT mode..."
echo ""

# Stop any existing containers
echo "📦 Stopping existing containers..."
docker-compose down 2>/dev/null
docker-compose -f docker-compose.dev.yml down 2>/dev/null

# Start development environment
echo "🔥 Starting development environment with hot reload..."
docker-compose -f docker-compose.dev.yml up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 15

# Check status
echo "📊 Service Status:"
docker-compose -f docker-compose.dev.yml ps

echo ""
echo "✅ Development environment is ready!"
echo ""
echo "🌐 Access URLs:"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080"
echo "   Database: localhost:3306"
echo ""
echo "🔑 Login Credentials:"
echo "   Username: admin"
echo "   Password: password"
echo ""
echo "💡 Hot Reload Features:"
echo "   • Frontend changes will auto-reload"
echo "   • Backend changes will auto-restart"
echo "   • No need to rebuild containers!"
echo ""
echo "🛑 To stop: docker-compose -f docker-compose.dev.yml down"
