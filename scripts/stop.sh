#!/bin/bash

echo "🛑 Stopping School Management System..."

# Stop all services
docker-compose down

echo "✅ All services stopped!"
echo ""
echo "📋 To remove all data (volumes), run: docker-compose down -v"
echo "📋 To start again, run: ./scripts/start.sh"
