#!/bin/bash

# Production Deployment Script for School Management System
# This script handles the complete deployment process

set -e  # Exit on any error

echo "🚀 Starting Production Deployment of School Management System..."
echo "================================================================"

# Check if running as root
if [ "$EUID" -eq 0 ]; then
    echo "❌ Error: Please don't run this script as root"
    echo "Use a regular user with sudo privileges"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "Please start Docker and try again"
    exit 1
fi

# Check if environment file exists
if [ ! -f "env.production" ]; then
    echo "❌ Error: env.production file not found"
    echo "Please create env.production file with your production configuration"
    exit 1
fi

# Load environment variables
echo "📋 Loading environment variables..."
source env.production

# Validate required environment variables
echo "🔍 Validating environment variables..."
required_vars=("MYSQL_ROOT_PASSWORD" "MYSQL_PASSWORD" "JWT_SECRET")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ] || [[ "${!var}" == *"CHANGE_THIS"* ]]; then
        echo "❌ Error: $var is not properly configured"
        echo "Please update env.production with secure values"
        exit 1
    fi
done

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose -f docker-compose.prod.yml down 2>/dev/null || true

# Pull latest changes (if in a git repository)
if [ -d ".git" ]; then
    echo "📥 Pulling latest changes..."
    git pull origin main
fi

# Build and start services
echo "🔨 Building and starting production services..."
docker-compose -f docker-compose.prod.yml up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Check service health
echo "🔍 Checking service health..."
docker-compose -f docker-compose.prod.yml ps

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
max_attempts=30
attempt=1
while [ $attempt -le $max_attempts ]; do
    if docker-compose -f docker-compose.prod.yml exec mysql mysqladmin ping -h localhost --silent; then
        echo "✅ Database is ready!"
        break
    fi
    echo "⏳ Waiting for database... (attempt $attempt/$max_attempts)"
    sleep 2
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ Error: Database failed to start within expected time"
    echo "Checking logs..."
    docker-compose -f docker-compose.prod.yml logs mysql
    exit 1
fi

# Wait for backend to be ready
echo "⏳ Waiting for backend to be ready..."
max_attempts=30
attempt=1
while [ $attempt -le $max_attempts ]; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ Backend is ready!"
        break
    fi
    echo "⏳ Waiting for backend... (attempt $attempt/$max_attempts)"
    sleep 2
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ Error: Backend failed to start within expected time"
    echo "Checking logs..."
    docker-compose -f docker-compose.prod.yml logs backend
    exit 1
fi

# Final health check
echo "🔍 Final health check..."
echo "Services Status:"
docker-compose -f docker-compose.prod.yml ps

echo ""
echo "✅ Production deployment completed successfully!"
echo ""
echo "🌐 Access URLs:"
echo "   Frontend: http://localhost (or your domain)"
echo "   Backend:  http://localhost:8080"
echo "   Health:   http://localhost:8080/actuator/health"
echo ""
echo "📊 Monitoring:"
echo "   View logs: docker-compose -f docker-compose.prod.yml logs -f"
echo "   Service status: docker-compose -f docker-compose.prod.yml ps"
echo ""
echo "🔒 Security Notes:"
echo "   • Change default passwords in env.production"
echo "   • Set up SSL certificates"
echo "   • Configure firewall rules"
echo "   • Set up regular backups"
echo ""
echo "🛑 To stop services: docker-compose -f docker-compose.prod.yml down"
