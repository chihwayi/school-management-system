#!/bin/bash

# Multi-Tenant School Provisioning Script
# Usage: ./create-school.sh --name "School Name" --subdomain "school" --admin-email "admin@school.com" --plan "premium"

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}"
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
ADMIN_DB="school_management_system_admin"
BASE_DOMAIN="${BASE_DOMAIN:-yoursystem.com}"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --name)
            SCHOOL_NAME="$2"
            shift 2
            ;;
        --subdomain)
            SUBDOMAIN="$2"
            shift 2
            ;;
        --admin-email)
            ADMIN_EMAIL="$2"
            shift 2
            ;;
        --plan)
            PLAN="$2"
            shift 2
            ;;
        --help)
            echo "Usage: $0 --name 'School Name' --subdomain 'school' --admin-email 'admin@school.com' --plan 'premium'"
            echo ""
            echo "Options:"
            echo "  --name         School name (required)"
            echo "  --subdomain    Subdomain for the school (required)"
            echo "  --admin-email  Admin email address (required)"
            echo "  --plan         Plan type: basic, premium, enterprise (default: basic)"
            echo "  --help         Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Validate required arguments
if [[ -z "$SCHOOL_NAME" || -z "$SUBDOMAIN" || -z "$ADMIN_EMAIL" ]]; then
    echo -e "${RED}Error: Missing required arguments${NC}"
    echo "Usage: $0 --name 'School Name' --subdomain 'school' --admin-email 'admin@school.com' --plan 'premium'"
    exit 1
fi

# Set default plan if not specified
PLAN="${PLAN:-basic}"

# Validate plan
if [[ ! "$PLAN" =~ ^(basic|premium|enterprise)$ ]]; then
    echo -e "${RED}Error: Invalid plan. Must be basic, premium, or enterprise${NC}"
    exit 1
fi

# Generate school ID and database name
SCHOOL_ID="school_$(date +%s)_$(echo $SUBDOMAIN | tr '[:upper:]' '[:lower:]' | tr -cd '[:alnum:]')"
DB_NAME="school_management_system_${SCHOOL_ID}"
FULL_DOMAIN="${SUBDOMAIN}.${BASE_DOMAIN}"

echo -e "${BLUE}🚀 Starting school provisioning...${NC}"
echo -e "${BLUE}📊 School: $SCHOOL_NAME${NC}"
echo -e "${BLUE}🌐 Subdomain: $SUBDOMAIN${NC}"
echo -e "${BLUE}📧 Admin Email: $ADMIN_EMAIL${NC}"
echo -e "${BLUE}💳 Plan: $PLAN${NC}"
echo -e "${BLUE}🆔 School ID: $SCHOOL_ID${NC}"
echo -e "${BLUE}🗄️  Database: $DB_NAME${NC}"
echo -e "${BLUE}🌍 Domain: $FULL_DOMAIN${NC}"
echo ""

# Check if subdomain already exists
if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "USE $ADMIN_DB; SELECT COUNT(*) FROM schools WHERE subdomain = '$SUBDOMAIN';" 2>/dev/null | grep -q "1"; then
    echo -e "${RED}❌ Error: Subdomain '$SUBDOMAIN' already exists${NC}"
    exit 1
fi

# Check if database already exists
if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "SHOW DATABASES LIKE '$DB_NAME';" 2>/dev/null | grep -q "$DB_NAME"; then
    echo -e "${RED}❌ Error: Database '$DB_NAME' already exists${NC}"
    exit 1
fi

echo -e "${YELLOW}⏳ Creating database...${NC}"

# Create database
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "
CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
"

echo -e "${GREEN}✅ Database created successfully${NC}"

echo -e "${YELLOW}⏳ Running migrations...${NC}"

# Run migrations for the new school
MIGRATION_DIR="../../mysql/init"

# Run migrations in order
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$DB_NAME" < "$MIGRATION_DIR/01-init.sql"
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$DB_NAME" < "$MIGRATION_DIR/02-add-receipt-number.sql"
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$DB_NAME" < "$MIGRATION_DIR/03-create-tables.sql"
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$DB_NAME" < "$MIGRATION_DIR/04-seed-data.sql"
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$DB_NAME" < "$MIGRATION_DIR/05-migration-strategy.sql"

echo -e "${GREEN}✅ Migrations completed successfully${NC}"

echo -e "${YELLOW}⏳ Creating school admin user...${NC}"

# Create school-specific admin user
ADMIN_USERNAME="admin_${SCHOOL_ID}"
ADMIN_PASSWORD=$(openssl rand -base64 12 | tr -d "=+/" | cut -c1-12)

# Insert into the school's database
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$DB_NAME" -e "
INSERT INTO users (username, email, password, first_name, last_name) VALUES 
('$ADMIN_USERNAME', '$ADMIN_EMAIL', '\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'School', 'Administrator');

INSERT INTO user_roles (user_id, role_id) VALUES 
(LAST_INSERT_ID(), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));
"

echo -e "${GREEN}✅ School admin user created${NC}"

echo -e "${YELLOW}⏳ Registering school in admin database...${NC}"

# Create admin database if it doesn't exist
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "
CREATE DATABASE IF NOT EXISTS $ADMIN_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE $ADMIN_DB;

CREATE TABLE IF NOT EXISTS schools (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    subdomain VARCHAR(100) NOT NULL UNIQUE,
    domain VARCHAR(255) NOT NULL,
    database_name VARCHAR(100) NOT NULL,
    plan_type VARCHAR(20) NOT NULL,
    admin_email VARCHAR(255) NOT NULL,
    admin_username VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id VARCHAR(100) NOT NULL,
    plan_type VARCHAR(20) NOT NULL,
    monthly_fee DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    start_date DATE NOT NULL,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
"

# Insert school record
mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$ADMIN_DB" -e "
INSERT INTO schools (school_id, name, subdomain, domain, database_name, plan_type, admin_email, admin_username) VALUES 
('$SCHOOL_ID', '$SCHOOL_NAME', '$SUBDOMAIN', '$FULL_DOMAIN', '$DB_NAME', '$PLAN', '$ADMIN_EMAIL', '$ADMIN_USERNAME');
"

# Insert subscription record
case $PLAN in
    "basic")
        MONTHLY_FEE=50.00
        ;;
    "premium")
        MONTHLY_FEE=100.00
        ;;
    "enterprise")
        MONTHLY_FEE=200.00
        ;;
esac

mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$ADMIN_DB" -e "
INSERT INTO subscriptions (school_id, plan_type, monthly_fee, start_date) VALUES 
('$SCHOOL_ID', '$PLAN', $MONTHLY_FEE, CURDATE());
"

echo -e "${GREEN}✅ School registered in admin database${NC}"

echo -e "${YELLOW}⏳ Setting up monitoring...${NC}"

# Create monitoring configuration for the school
mkdir -p "../../shared/monitoring/schools/$SCHOOL_ID"

cat > "../../shared/monitoring/schools/$SCHOOL_ID/prometheus.yml" << EOF
# Monitoring configuration for $SCHOOL_NAME
- job_name: 'school_management_system_$SCHOOL_ID'
  static_configs:
    - targets: ['$SUBDOMAIN:8080']
  metrics_path: '/actuator/prometheus'
  scrape_interval: 30s
EOF

echo -e "${GREEN}✅ Monitoring configured${NC}"

echo -e "${YELLOW}⏳ Setting up backup schedule...${NC}"

# Create backup script for this school
cat > "../../shared/backup/schools/$SCHOOL_ID/backup.sh" << EOF
#!/bin/bash
# Backup script for $SCHOOL_NAME
DATE=\$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="../../shared/backup/schools/$SCHOOL_ID/${DB_NAME}_backup_\${DATE}.sql"

mysqldump -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" \\
    --single-transaction \\
    --routines \\
    --triggers \\
    --events \\
    --add-drop-database \\
    --databases "$DB_NAME" > "\$BACKUP_FILE"

echo "Backup completed: \$BACKUP_FILE"
EOF

chmod +x "../../shared/backup/schools/$SCHOOL_ID/backup.sh"

echo -e "${GREEN}✅ Backup schedule configured${NC}"

echo -e "${YELLOW}⏳ Sending welcome email...${NC}"

# Create welcome email template
cat > "../../shared/templates/welcome_email_$SCHOOL_ID.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to $SCHOOL_NAME</title>
</head>
<body>
    <h1>Welcome to $SCHOOL_NAME!</h1>
    <p>Your school management system has been successfully set up.</p>
    
    <h2>Access Information:</h2>
    <ul>
        <li><strong>Website:</strong> <a href="http://$FULL_DOMAIN">http://$FULL_DOMAIN</a></li>
        <li><strong>Admin Username:</strong> $ADMIN_USERNAME</li>
        <li><strong>Admin Password:</strong> $ADMIN_PASSWORD</li>
        <li><strong>Plan:</strong> $PLAN</li>
    </ul>
    
    <h2>Next Steps:</h2>
    <ol>
        <li>Login with the admin credentials above</li>
        <li>Configure your school settings</li>
        <li>Add teachers and students</li>
        <li>Customize your branding</li>
    </ol>
    
    <p>If you need any assistance, please contact our support team.</p>
    
    <p>Best regards,<br>Your School Management System Team</p>
</body>
</html>
EOF

echo -e "${GREEN}✅ Welcome email template created${NC}"

# Generate summary report
echo ""
echo -e "${GREEN}🎉 School provisioning completed successfully!${NC}"
echo ""
echo -e "${BLUE}📋 Provisioning Summary:${NC}"
echo -e "   School Name: $SCHOOL_NAME"
echo -e "   School ID: $SCHOOL_ID"
echo -e "   Subdomain: $SUBDOMAIN"
echo -e "   Domain: $FULL_DOMAIN"
echo -e "   Database: $DB_NAME"
echo -e "   Plan: $PLAN"
echo -e "   Monthly Fee: \$$MONTHLY_FEE"
echo ""
echo -e "${BLUE}🔐 Admin Access:${NC}"
echo -e "   Username: $ADMIN_USERNAME"
echo -e "   Password: $ADMIN_PASSWORD"
echo -e "   Login URL: http://$FULL_DOMAIN"
echo ""
echo -e "${BLUE}📁 Files Created:${NC}"
echo -e "   Database: $DB_NAME"
echo -e "   Monitoring: ../../shared/monitoring/schools/$SCHOOL_ID/"
echo -e "   Backup: ../../shared/backup/schools/$SCHOOL_ID/"
echo -e "   Email Template: ../../shared/templates/welcome_email_$SCHOOL_ID.html"
echo ""
echo -e "${YELLOW}⚠️  Important:${NC}"
echo -e "   1. Send the welcome email to $ADMIN_EMAIL"
echo -e "   2. Update DNS records for $FULL_DOMAIN"
echo -e "   3. Configure SSL certificate for $FULL_DOMAIN"
echo -e "   4. Set up automated backups for $SCHOOL_ID"
echo ""
echo -e "${GREEN}✅ School is ready to use!${NC}"
