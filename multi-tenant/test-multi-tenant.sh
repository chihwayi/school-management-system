#!/bin/bash

# Multi-Tenant System Test Script
# This script tests the complete multi-tenant setup

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 Testing Multi-Tenant School Management System${NC}"
echo "=================================================="

# Configuration
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}"
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
ADMIN_DB="school_management_system_admin"
BASE_DOMAIN="${BASE_DOMAIN:-yoursystem.com}"

# Test functions
test_database_connection() {
    echo -e "\n${YELLOW}📊 Testing Database Connection...${NC}"
    
    if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "SELECT 1;" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Database connection successful${NC}"
        return 0
    else
        echo -e "${RED}❌ Database connection failed${NC}"
        return 1
    fi
}

test_admin_database() {
    echo -e "\n${YELLOW}🏢 Testing Admin Database...${NC}"
    
    # Check if admin database exists
    if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "USE $ADMIN_DB;" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Admin database exists${NC}"
        
        # Check if schools table exists
        if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "USE $ADMIN_DB; DESCRIBE schools;" >/dev/null 2>&1; then
            echo -e "${GREEN}✅ Schools table exists${NC}"
            
            # Count schools
            SCHOOL_COUNT=$(mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "USE $ADMIN_DB; SELECT COUNT(*) FROM schools;" 2>/dev/null | tail -n 1)
            echo -e "${GREEN}✅ Found $SCHOOL_COUNT schools in admin database${NC}"
        else
            echo -e "${RED}❌ Schools table does not exist${NC}"
            return 1
        fi
    else
        echo -e "${RED}❌ Admin database does not exist${NC}"
        return 1
    fi
}

test_school_databases() {
    echo -e "\n${YELLOW}🏫 Testing School Databases...${NC}"
    
    # Get list of schools from admin database
    SCHOOLS=$(mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "USE $ADMIN_DB; SELECT database_name FROM schools WHERE status = 'active';" 2>/dev/null | tail -n +2)
    
    if [ -z "$SCHOOLS" ]; then
        echo -e "${YELLOW}⚠️  No active schools found${NC}"
        return 0
    fi
    
    for DB in $SCHOOLS; do
        if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -e "USE $DB;" >/dev/null 2>&1; then
            echo -e "${GREEN}✅ Database $DB exists${NC}"
        else
            echo -e "${RED}❌ Database $DB does not exist${NC}"
        fi
    done
}

test_admin_panel_backend() {
    echo -e "\n${YELLOW}🔧 Testing Admin Panel Backend...${NC}"
    
    # Test if admin backend is running
    if curl -f http://localhost:8081/api/health >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Admin backend is running on port 8081${NC}"
        
        # Test dashboard endpoint
        if curl -f http://localhost:8081/api/dashboard/stats >/dev/null 2>&1; then
            echo -e "${GREEN}✅ Dashboard API endpoint is working${NC}"
        else
            echo -e "${YELLOW}⚠️  Dashboard API endpoint not responding${NC}"
        fi
        
        # Test schools endpoint
        if curl -f http://localhost:8081/api/schools >/dev/null 2>&1; then
            echo -e "${GREEN}✅ Schools API endpoint is working${NC}"
        else
            echo -e "${YELLOW}⚠️  Schools API endpoint not responding${NC}"
        fi
    else
        echo -e "${RED}❌ Admin backend is not running on port 8081${NC}"
        return 1
    fi
}

test_admin_panel_frontend() {
    echo -e "\n${YELLOW}🎨 Testing Admin Panel Frontend...${NC}"
    
    # Test if admin frontend is running
    if curl -f http://localhost:8082 >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Admin frontend is running on port 8082${NC}"
    else
        echo -e "${RED}❌ Admin frontend is not running on port 8082${NC}"
        return 1
    fi
}

test_nginx_proxy() {
    echo -e "\n${YELLOW}🌐 Testing Nginx Proxy...${NC}"
    
    # Test if nginx is running
    if curl -f http://localhost:80/health >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Nginx proxy is running on port 80${NC}"
    else
        echo -e "${RED}❌ Nginx proxy is not running on port 80${NC}"
        return 1
    fi
}

test_school_provisioning() {
    echo -e "\n${YELLOW}🚀 Testing School Provisioning...${NC}"
    
    # Test the create-school script
    if [ -f "tenant-manager/scripts/create-school.sh" ]; then
        echo -e "${GREEN}✅ School provisioning script exists${NC}"
        
        # Test script syntax
        if bash -n tenant-manager/scripts/create-school.sh; then
            echo -e "${GREEN}✅ School provisioning script syntax is valid${NC}"
        else
            echo -e "${RED}❌ School provisioning script has syntax errors${NC}"
            return 1
        fi
    else
        echo -e "${RED}❌ School provisioning script not found${NC}"
        return 1
    fi
}

test_docker_services() {
    echo -e "\n${YELLOW}🐳 Testing Docker Services...${NC}"
    
    # Check if docker is running
    if ! docker info >/dev/null 2>&1; then
        echo -e "${RED}❌ Docker is not running${NC}"
        return 1
    fi
    
    echo -e "${GREEN}✅ Docker is running${NC}"
    
    # Check running containers
    CONTAINERS=$(docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}")
    echo -e "\n${BLUE}📋 Running Containers:${NC}"
    echo "$CONTAINERS"
}

# Run all tests
main() {
    local failed_tests=0
    
    test_database_connection || ((failed_tests++))
    test_admin_database || ((failed_tests++))
    test_school_databases || ((failed_tests++))
    test_admin_panel_backend || ((failed_tests++))
    test_admin_panel_frontend || ((failed_tests++))
    test_nginx_proxy || ((failed_tests++))
    test_school_provisioning || ((failed_tests++))
    test_docker_services || ((failed_tests++))
    
    echo -e "\n${BLUE}📊 Test Summary${NC}"
    echo "=================="
    
    if [ $failed_tests -eq 0 ]; then
        echo -e "${GREEN}🎉 All tests passed! Multi-tenant system is ready.${NC}"
        echo -e "\n${BLUE}🌐 Access Points:${NC}"
        echo -e "  • Admin Panel: http://localhost:8082"
        echo -e "  • Admin API: http://localhost:8081/api"
        echo -e "  • Main Proxy: http://localhost:80"
        echo -e "\n${BLUE}📚 Next Steps:${NC}"
        echo -e "  1. Access the admin panel at http://localhost:8082"
        echo -e "  2. Create a new school using the admin interface"
        echo -e "  3. Test the school provisioning process"
        echo -e "  4. Verify school isolation and functionality"
    else
        echo -e "${RED}❌ $failed_tests test(s) failed. Please check the issues above.${NC}"
        echo -e "\n${YELLOW}🔧 Troubleshooting:${NC}"
        echo -e "  1. Ensure all Docker containers are running: docker-compose ps"
        echo -e "  2. Check container logs: docker-compose logs"
        echo -e "  3. Verify database connection and initialization"
        echo -e "  4. Check if all required ports are available"
    fi
    
    return $failed_tests
}

# Run the main function
main


