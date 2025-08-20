#!/bin/bash

# Database Backup Script for School Management System
# Usage: ./backup.sh [database_name] [backup_directory]

set -e

# Configuration
DEFAULT_DB="school_management_system"
DEFAULT_BACKUP_DIR="./backups"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-root}"
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"

# Parse arguments
DB_NAME="${1:-$DEFAULT_DB}"
BACKUP_DIR="${2:-$DEFAULT_BACKUP_DIR}"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Generate timestamp
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/${DB_NAME}_backup_${TIMESTAMP}.sql"

echo "🚀 Starting database backup..."
echo "📊 Database: $DB_NAME"
echo "📁 Backup file: $BACKUP_FILE"
echo "⏰ Timestamp: $TIMESTAMP"

# Perform backup
if [ -n "$MYSQL_PASSWORD" ]; then
    mysqldump \
        --host="$MYSQL_HOST" \
        --port="$MYSQL_PORT" \
        --user="$MYSQL_USER" \
        --password="$MYSQL_PASSWORD" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        --add-drop-database \
        --databases "$DB_NAME" > "$BACKUP_FILE"
else
    mysqldump \
        --host="$MYSQL_HOST" \
        --port="$MYSQL_PORT" \
        --user="$MYSQL_USER" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        --add-drop-database \
        --databases "$DB_NAME" > "$BACKUP_FILE"
fi

# Check if backup was successful
if [ $? -eq 0 ]; then
    # Get file size
    FILE_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    
    echo "✅ Backup completed successfully!"
    echo "📏 File size: $FILE_SIZE"
    echo "📄 Backup file: $BACKUP_FILE"
    
    # Create a symlink to the latest backup
    LATEST_LINK="$BACKUP_DIR/${DB_NAME}_latest.sql"
    ln -sf "$BACKUP_FILE" "$LATEST_LINK"
    echo "🔗 Latest backup link: $LATEST_LINK"
    
    # Keep only last 10 backups
    echo "🧹 Cleaning old backups (keeping last 10)..."
    ls -t "$BACKUP_DIR"/${DB_NAME}_backup_*.sql | tail -n +11 | xargs -r rm
    
    echo "📋 Backup summary:"
    echo "   - Database: $DB_NAME"
    echo "   - Backup file: $BACKUP_FILE"
    echo "   - File size: $FILE_SIZE"
    echo "   - Timestamp: $TIMESTAMP"
    
else
    echo "❌ Backup failed!"
    exit 1
fi
