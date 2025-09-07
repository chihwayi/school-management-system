#!/bin/bash

# System Monitoring Script for School Management System
# This script provides comprehensive monitoring of the system

echo "📊 School Management System - System Status"
echo "==========================================="
echo ""

# Check Docker status
echo "🐳 Docker Status:"
if docker info > /dev/null 2>&1; then
    echo "   ✅ Docker is running"
    echo "   Version: $(docker --version | cut -d' ' -f3 | cut -d',' -f1)"
else
    echo "   ❌ Docker is not running"
fi
echo ""

# Check service status
echo "🔧 Service Status:"
if [ -f "docker-compose.prod.yml" ]; then
    echo "   Production Services:"
    docker-compose -f docker-compose.prod.yml ps 2>/dev/null || echo "   No production services running"
elif [ -f "docker-compose.yml" ]; then
    echo "   Development Services:"
    docker-compose ps 2>/dev/null || echo "   No services running"
else
    echo "   No docker-compose files found"
fi
echo ""

# Check system resources
echo "💻 System Resources:"
echo "   CPU Load: $(uptime | awk -F'load average:' '{print $2}')"
echo "   Memory Usage:"
free -h | grep -E "Mem|Swap" | while read line; do
    echo "     $line"
done
echo ""

# Check disk usage
echo "💾 Disk Usage:"
df -h | grep -E "/$|/var|/opt" | while read line; do
    echo "   $line"
done
echo ""

# Check network connectivity
echo "🌐 Network Connectivity:"
if ping -c 1 google.com > /dev/null 2>&1; then
    echo "   ✅ Internet connection: OK"
else
    echo "   ❌ Internet connection: Failed"
fi

# Check if services are responding
echo ""
echo "🔍 Service Health Checks:"
if [ -f "docker-compose.prod.yml" ]; then
    # Check backend health
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "   ✅ Backend API: Healthy"
    else
        echo "   ❌ Backend API: Unhealthy"
    fi
    
    # Check frontend
    if curl -f http://localhost:80 > /dev/null 2>&1; then
        echo "   ✅ Frontend: Healthy"
    else
        echo "   ❌ Frontend: Unhealthy"
    fi
    
    # Check database
    if docker-compose -f docker-compose.prod.yml exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        echo "   ✅ Database: Healthy"
    else
        echo "   ❌ Database: Unhealthy"
    fi
else
    echo "   No production services detected"
fi
echo ""

# Check recent logs for errors
echo "📋 Recent Error Logs (last 10 lines):"
if [ -f "docker-compose.prod.yml" ]; then
    docker-compose -f docker-compose.prod.yml logs --tail=10 2>/dev/null | grep -i error || echo "   No recent errors found"
else
    docker-compose logs --tail=10 2>/dev/null | grep -i error || echo "   No recent errors found"
fi
echo ""

# Check backup status
echo "💾 Backup Status:"
BACKUP_DIR="./mysql/backup"
if [ -d "$BACKUP_DIR" ]; then
    BACKUP_COUNT=$(ls -1 "$BACKUP_DIR"/school_backup_*.sql.gz 2>/dev/null | wc -l)
    if [ $BACKUP_COUNT -gt 0 ]; then
        LATEST_BACKUP=$(ls -t "$BACKUP_DIR"/school_backup_*.sql.gz 2>/dev/null | head -1)
        BACKUP_DATE=$(stat -c %y "$LATEST_BACKUP" 2>/dev/null | cut -d' ' -f1)
        echo "   ✅ Backups available: $BACKUP_COUNT"
        echo "   📅 Latest backup: $BACKUP_DATE"
    else
        echo "   ⚠️  No backups found"
    fi
else
    echo "   ⚠️  Backup directory not found"
fi
echo ""

# Security check
echo "🔒 Security Status:"
echo "   Environment files:"
if [ -f "env.production" ]; then
    if grep -q "CHANGE_THIS" env.production; then
        echo "     ⚠️  Production environment needs configuration"
    else
        echo "     ✅ Production environment configured"
    fi
else
    echo "     ❌ Production environment file not found"
fi

if [ -f "env.development" ]; then
    echo "     ✅ Development environment file exists"
else
    echo "     ⚠️  Development environment file not found"
fi
echo ""

# Recommendations
echo "💡 Recommendations:"
if ! crontab -l 2>/dev/null | grep -q "backup-db.sh"; then
    echo "   • Set up automated database backups"
fi

if ! systemctl is-active --quiet ufw; then
    echo "   • Consider enabling firewall (UFW)"
fi

if [ ! -f "/etc/letsencrypt/live/yourdomain.com/fullchain.pem" ]; then
    echo "   • Set up SSL certificates for production"
fi

echo ""
echo "✅ System monitoring completed!"
echo ""
echo "🛠️  Useful Commands:"
echo "   View logs: docker-compose -f docker-compose.prod.yml logs -f"
echo "   Restart services: docker-compose -f docker-compose.prod.yml restart"
echo "   Create backup: ./scripts/backup-db.sh"
echo "   Deploy: ./scripts/deploy-prod.sh"
