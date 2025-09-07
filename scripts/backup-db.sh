#!/bin/bash

# Database Backup Script for School Management System
# This script creates automated backups of the MySQL database

set -e  # Exit on any error

echo "📦 Creating Database Backup..."
echo "=============================="

# Configuration
BACKUP_DIR="./mysql/backup"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="school_backup_$DATE.sql"
RETENTION_DAYS=7

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Load environment variables
if [ -f "env.production" ]; then
    source env.production
elif [ -f "env.development" ]; then
    source env.development
else
    echo "❌ Error: No environment file found"
    echo "Please ensure env.production or env.development exists"
    exit 1
fi

# Check if MySQL container is running
if ! docker-compose ps mysql | grep -q "Up"; then
    echo "❌ Error: MySQL container is not running"
    echo "Please start the services first: docker-compose up -d"
    exit 1
fi

echo "📋 Backup Configuration:"
echo "   Database: $MYSQL_DATABASE"
echo "   Backup File: $BACKUP_FILE"
echo "   Backup Directory: $BACKUP_DIR"
echo "   Retention: $RETENTION_DAYS days"
echo ""

# Create backup
echo "🔄 Creating backup..."
docker-compose exec mysql mysqldump \
    -u root \
    -p"$MYSQL_ROOT_PASSWORD" \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    --hex-blob \
    --add-drop-database \
    --databases "$MYSQL_DATABASE" > "$BACKUP_DIR/$BACKUP_FILE"

# Check if backup was successful
if [ $? -eq 0 ] && [ -f "$BACKUP_DIR/$BACKUP_FILE" ]; then
    BACKUP_SIZE=$(du -h "$BACKUP_DIR/$BACKUP_FILE" | cut -f1)
    echo "✅ Backup created successfully!"
    echo "   File: $BACKUP_FILE"
    echo "   Size: $BACKUP_SIZE"
    echo "   Location: $BACKUP_DIR/$BACKUP_FILE"
else
    echo "❌ Error: Backup failed"
    exit 1
fi

# Compress backup
echo "🗜️  Compressing backup..."
gzip "$BACKUP_DIR/$BACKUP_FILE"
COMPRESSED_FILE="$BACKUP_FILE.gz"
COMPRESSED_SIZE=$(du -h "$BACKUP_DIR/$COMPRESSED_FILE" | cut -f1)
echo "✅ Backup compressed!"
echo "   Compressed File: $COMPRESSED_FILE"
echo "   Compressed Size: $COMPRESSED_SIZE"

# Clean up old backups
echo "🧹 Cleaning up old backups..."
find "$BACKUP_DIR" -name "school_backup_*.sql.gz" -mtime +$RETENTION_DAYS -delete
echo "✅ Old backups cleaned up (kept last $RETENTION_DAYS days)"

# List current backups
echo ""
echo "📋 Current Backups:"
ls -lh "$BACKUP_DIR"/school_backup_*.sql.gz 2>/dev/null || echo "   No backups found"

echo ""
echo "✅ Database backup completed successfully!"
echo ""
echo "💡 Tips:"
echo "   • Set up a cron job to run this script automatically"
echo "   • Test restore procedures regularly"
echo "   • Store backups in a secure, off-site location"
echo ""
echo "🔄 To restore from backup:"
echo "   gunzip $BACKUP_DIR/$COMPRESSED_FILE"
echo "   docker-compose exec -T mysql mysql -u root -p'$MYSQL_ROOT_PASSWORD' < $BACKUP_DIR/$BACKUP_FILE"
