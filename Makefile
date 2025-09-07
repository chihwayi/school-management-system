.PHONY: help start stop dev clean logs status build rebuild

# Default target
help:
	@echo "School Management System - Docker Commands"
	@echo ""
	@echo "Available commands:"
	@echo "  make start       - Start all services (production mode)"
	@echo "  make dev         - Start all services (development mode with hot reload)"
	@echo "  make stop        - Stop all services"
	@echo "  make clean       - Stop and remove all containers, images, and volumes"
	@echo "  make logs        - View logs from all services"
	@echo "  make status      - Show status of all services"
	@echo "  make build       - Build all Docker images"
	@echo "  make rebuild     - Rebuild all Docker images (no cache)"
	@echo ""
	@echo "Production commands:"
	@echo "  make deploy-prod - Deploy to production"
	@echo "  make backup      - Create database backup"
	@echo "  make monitor     - Monitor system status"
	@echo "  make logs-prod   - View production logs"
	@echo "  make status-prod - Show production status"
	@echo ""
	@echo "  make help        - Show this help message"

# Production mode
start:
	@echo "🚀 Starting School Management System in production mode..."
	@./scripts/deploy-prod.sh

# Development mode
dev:
	@echo "🚀 Starting School Management System in development mode..."
	@./scripts/dev.sh

# Stop services
stop:
	@echo "🛑 Stopping School Management System..."
	@docker-compose down

# Clean everything
clean:
	@echo "🧹 Cleaning up School Management System..."
	@./scripts/clean.sh

# View logs
logs:
	@echo "📋 Viewing logs..."
	@docker-compose logs -f

# Show service status
service-status:
	@echo "🔍 Service status:"
	@docker-compose ps

# Build images
build:
	@echo "📦 Building Docker images..."
	@docker-compose build

# Rebuild images (no cache)
rebuild:
	@echo "📦 Rebuilding Docker images (no cache)..."
	@docker-compose build --no-cache

# Frontend logs
logs-frontend:
	@docker-compose logs -f frontend

# Backend logs
logs-backend:
	@docker-compose logs -f backend

# Database logs
logs-mysql:
	@docker-compose logs -f mysql

# Shell into frontend container
shell-frontend:
	@docker-compose exec frontend sh

# Shell into backend container
shell-backend:
	@docker-compose exec backend sh

# Shell into database container
shell-mysql:
	@docker-compose exec mysql mysql -u root -p

# Restart services
restart:
	@echo "🔄 Restarting services..."
	@docker-compose restart

# Update and restart
update:
	@echo "🔄 Updating and restarting services..."
	@docker-compose pull
	@docker-compose up -d --build

# Create a new release
release:
	@echo "🚀 Creating a new release..."
	@./scripts/release.sh

# Show git status
git-status:
	@echo "📊 Git status:"
	@git status

# Show recent commits
log:
	@echo "📝 Recent commits:"
	@git log --oneline -10

# Production deployment
deploy-prod:
	@echo "🚀 Deploying to production..."
	@./scripts/deploy-prod.sh

# Create database backup
backup:
	@echo "📦 Creating database backup..."
	@./scripts/backup-db.sh

# Monitor system
monitor:
	@echo "📊 Monitoring system..."
	@./scripts/monitor.sh

# Production logs
logs-prod:
	@echo "📋 Production logs:"
	@docker-compose -f docker-compose.prod.yml logs -f

# Production status
status-prod:
	@echo "🔍 Production status:"
	@docker-compose -f docker-compose.prod.yml ps
