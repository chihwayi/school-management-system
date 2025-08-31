#!/bin/bash

echo "🚀 Starting School Management System in PRODUCTION mode..."
echo ""

# Stop any existing containers
echo "📦 Stopping existing containers..."
docker-compose down 2>/dev/null
docker-compose -f docker-compose.dev.yml down 2>/dev/null

# Start production environment
echo "🔥 Starting production environment..."
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 15

# Check status
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "✅ Production environment is ready!"
echo ""
echo "🌐 Access URLs:"
echo "   Frontend: http://localhost:80"
echo "   Backend:  http://localhost:8080"
echo "   Database: localhost:3306"
echo ""
echo "🔑 Login Credentials:"
echo "   Username: admin"
echo "   Password: password"
echo ""
echo "⚠️  Production Mode:"
echo "   • Changes require container rebuilds"
echo "   • Optimized for performance"
echo "   • Use ./dev.sh for development"
echo ""
echo "🛑 To stop: docker-compose down"
