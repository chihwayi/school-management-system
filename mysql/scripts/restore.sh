#!/bin/bash

# Database Restore Script for School Management System
# Usage: ./restore.sh [backup_file] [database_name]

set -e

# Configuration
DEFAULT_DB="school_management_system"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-root}"
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"

# Parse arguments
BACKUP_FILE="$1"
DB_NAME="${2:-$DEFAULT_DB}"

# Validate arguments
if [ -z "$BACKUP_FILE" ]; then
    echo "❌ Error: Backup file not specified"
    echo "Usage: $0 <backup_file> [database_name]"
    echo "Example: $0 ./backups/school_management_system_backup_20250817_143022.sql"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Error: Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "🚀 Starting database restore..."
echo "📄 Backup file: $BACKUP_FILE"
echo "📊 Target database: $DB_NAME"
echo "⚠️  This will overwrite the existing database!"

# Confirm before proceeding
read -p "Are you sure you want to proceed? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Restore cancelled"
    exit 1
fi

# Create backup before restore
echo "📦 Creating backup before restore..."
RESTORE_BACKUP_DIR="./backups/pre_restore"
mkdir -p "$RESTORE_BACKUP_DIR"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
PRE_RESTORE_BACKUP="$RESTORE_BACKUP_DIR/${DB_NAME}_pre_restore_${TIMESTAMP}.sql"

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
        --databases "$DB_NAME" > "$PRE_RESTORE_BACKUP" 2>/dev/null || echo "⚠️  Could not create pre-restore backup (database might not exist)"
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
        --databases "$DB_NAME" > "$PRE_RESTORE_BACKUP" 2>/dev/null || echo "⚠️  Could not create pre-restore backup (database might not exist)"
fi

# Perform restore
echo "🔄 Restoring database..."
if [ -n "$MYSQL_PASSWORD" ]; then
    mysql \
        --host="$MYSQL_HOST" \
        --port="$MYSQL_PORT" \
        --user="$MYSQL_USER" \
        --password="$MYSQL_PASSWORD" < "$BACKUP_FILE"
else
    mysql \
        --host="$MYSQL_HOST" \
        --port="$MYSQL_PORT" \
        --user="$MYSQL_USER" < "$BACKUP_FILE"
fi

# Check if restore was successful
if [ $? -eq 0 ]; then
    echo "✅ Database restore completed successfully!"
    
    # Verify restore
    echo "🔍 Verifying restore..."
    if [ -n "$MYSQL_PASSWORD" ]; then
        TABLE_COUNT=$(mysql \
            --host="$MYSQL_HOST" \
            --port="$MYSQL_PORT" \
            --user="$MYSQL_USER" \
            --password="$MYSQL_PASSWORD" \
            --silent \
            --skip-column-names \
            -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '$DB_NAME';")
    else
        TABLE_COUNT=$(mysql \
            --host="$MYSQL_HOST" \
            --port="$MYSQL_PORT" \
            --user="$MYSQL_USER" \
            --silent \
            --skip-column-names \
            -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '$DB_NAME';")
    fi
    
    echo "📊 Tables restored: $TABLE_COUNT"
    
    # Check key tables
    echo "🔍 Checking key tables..."
    if [ -n "$MYSQL_PASSWORD" ]; then
        mysql \
            --host="$MYSQL_HOST" \
            --port="$MYSQL_PORT" \
            --user="$MYSQL_USER" \
            --password="$MYSQL_PASSWORD" \
            --silent \
            -e "
            SELECT 'Users' as table_name, COUNT(*) as count FROM $DB_NAME.users
            UNION ALL
            SELECT 'Students' as table_name, COUNT(*) as count FROM $DB_NAME.students
            UNION ALL
            SELECT 'Teachers' as table_name, COUNT(*) as count FROM $DB_NAME.teachers
            UNION ALL
            SELECT 'Fee Payments' as table_name, COUNT(*) as count FROM $DB_NAME.fee_payments;
            "
    else
        mysql \
            --host="$MYSQL_HOST" \
            --port="$MYSQL_PORT" \
            --user="$MYSQL_USER" \
            --silent \
            -e "
            SELECT 'Users' as table_name, COUNT(*) as count FROM $DB_NAME.users
            UNION ALL
            SELECT 'Students' as table_name, COUNT(*) as count FROM $DB_NAME.students
            UNION ALL
            SELECT 'Teachers' as table_name, COUNT(*) as count FROM $DB_NAME.teachers
            UNION ALL
            SELECT 'Fee Payments' as table_name, COUNT(*) as count FROM $DB_NAME.fee_payments;
            "
    fi
    
    echo "📋 Restore summary:"
    echo "   - Backup file: $BACKUP_FILE"
    echo "   - Target database: $DB_NAME"
    echo "   - Tables restored: $TABLE_COUNT"
    echo "   - Pre-restore backup: $PRE_RESTORE_BACKUP"
    
else
    echo "❌ Database restore failed!"
    exit 1
fi
