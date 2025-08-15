#!/bin/bash

echo "🧹 Cleaning up School Management System..."

# Stop and remove containers, networks, and volumes
docker-compose down -v

# Remove images
docker rmi school-management-frontend:latest 2>/dev/null || true
docker rmi school-management-backend:latest 2>/dev/null || true

# Remove any dangling images
docker image prune -f

echo "✅ Cleanup completed!"
echo ""
echo "📋 To start fresh, run: ./scripts/start.sh"
