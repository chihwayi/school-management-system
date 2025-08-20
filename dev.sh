#!/bin/bash

echo "🚀 Starting Bulletproof School Management System"
echo "=============================================="

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  Port $port is already in use. Stopping process..."
        lsof -ti:$port | xargs kill -9
        sleep 2
    fi
}

# Function to check if container exists and remove it
cleanup_container() {
    local container_name=$1
    if docker ps -a --format "table {{.Names}}" | grep -q "^$container_name$"; then
        echo "🧹 Cleaning up existing container: $container_name"
        docker rm -f $container_name 2>/dev/null || true
    fi
}

echo "🧹 Cleaning up existing containers and processes..."

# Stop any existing containers
docker-compose down --remove-orphans 2>/dev/null || true

# Clean up any existing containers
cleanup_container "school_mysql"
cleanup_container "school_backend"
cleanup_container "admin_backend"
cleanup_container "school_frontend"
cleanup_container "admin_frontend"
cleanup_container "school_nginx"

# Check and free up ports
check_port 3000
check_port 5173
check_port 8080
check_port 8081
check_port 3306
check_port 8000

echo "🔨 Starting bulletproof system..."

# Start MySQL first and wait for it to be healthy
echo "📊 Starting MySQL database..."
docker-compose up -d mysql

echo "⏳ Waiting for MySQL to be ready..."
timeout=60
counter=0
while [ $counter -lt $timeout ]; do
    if docker-compose ps mysql | grep -q "healthy"; then
        echo "✅ MySQL is healthy!"
        break
    fi
    echo "⏳ Waiting for MySQL... ($counter/$timeout)"
    sleep 2
    counter=$((counter + 2))
done

if [ $counter -eq $timeout ]; then
    echo "❌ MySQL failed to start properly"
    exit 1
fi

# Start backends
echo "🏫 Starting School Backend..."
docker-compose up -d school-backend

echo "👔 Starting Admin Backend..."
docker-compose up -d admin-backend

# Wait for backends to be healthy
echo "⏳ Waiting for backends to be ready..."
timeout=120
counter=0
while [ $counter -lt $timeout ]; do
    school_healthy=$(docker-compose ps school-backend | grep -c "healthy" || echo "0")
    admin_healthy=$(docker-compose ps admin-backend | grep -c "healthy" || echo "0")
    
    if [ "$school_healthy" -eq 1 ] && [ "$admin_healthy" -eq 1 ]; then
        echo "✅ Both backends are healthy!"
        break
    fi
    
    echo "⏳ Waiting for backends... School: $school_healthy, Admin: $admin_healthy ($counter/$timeout)"
    sleep 5
    counter=$((counter + 5))
done

if [ $counter -eq $timeout ]; then
    echo "❌ Backends failed to start properly"
    docker-compose logs school-backend --tail=10
    docker-compose logs admin-backend --tail=10
    exit 1
fi

# Start frontends
echo "🎨 Starting School Frontend..."
docker-compose up -d school-frontend

echo "🎨 Starting Admin Frontend..."
docker-compose up -d admin-frontend

# Start nginx
echo "🌐 Starting Nginx..."
docker-compose up -d nginx

echo "⏳ Final wait for all services..."
sleep 15

echo "🔍 Checking final status..."
docker-compose ps

echo ""
echo "🎉 BULLETPROOF SYSTEM READY!"
echo "=============================================="
echo ""
echo "📱 FRONTEND DEVELOPMENT SERVERS (LIVE RELOAD):"
echo "   🏫 School System: http://localhost:3000"
echo "   👔 Admin Panel: http://localhost:5173"
echo ""
echo "🌐 NGINX PROXY:"
echo "   🏫 School System: http://localhost:8000"
echo "   👔 Admin Panel: http://admin.localhost:8000"
echo ""
echo "🔧 BACKEND APIs:"
echo "   🏫 School API: http://localhost:8080/api"
echo "   👔 Admin API: http://localhost:8081/api"
echo ""
echo "💾 DATABASE:"
echo "   📊 MySQL: localhost:3306"
echo ""
echo "🔑 LOGIN CREDENTIALS:"
echo "   Admin Panel: admin / password"
echo "   School System: admin / password"
echo ""
echo "📝 DEVELOPMENT NOTES:"
echo "   ✅ Frontend changes auto-reload (no rebuild needed!)"
echo "   ✅ Hot module replacement enabled"
echo "   ✅ Real-time file watching"
echo "   ✅ Bulletproof startup sequence"
echo ""
echo "🔧 COMMANDS:"
echo "   • View logs: docker-compose logs -f"
echo "   • Stop all: docker-compose down"
echo "   • Restart: ./dev.sh"
echo ""
echo "🚀 Happy coding! Your changes will be reflected immediately!"
