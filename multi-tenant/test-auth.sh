#!/bin/bash

# Test Authentication System Script
# This script tests the admin panel authentication flow

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔐 Testing Admin Panel Authentication System${NC}"
echo "=================================================="

# Check if services are running
echo -e "\n${YELLOW}🔍 Checking service status...${NC}"
if docker-compose -f test-admin-panel.yml ps | grep -q "Up"; then
    echo -e "${GREEN}✅ All services are running${NC}"
else
    echo -e "${RED}❌ Services are not running. Please start them first.${NC}"
    exit 1
fi

# Test frontend accessibility
echo -e "\n${YELLOW}🎨 Testing Frontend...${NC}"
if curl -f http://localhost:8082 >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Frontend is accessible${NC}"
else
    echo -e "${RED}❌ Frontend is not accessible${NC}"
fi

# Test backend API
echo -e "\n${YELLOW}🔧 Testing Backend API...${NC}"
if curl -f http://localhost:8081/api/actuator/health >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Backend API is accessible${NC}"
else
    echo -e "${RED}❌ Backend API is not accessible${NC}"
fi

# Test database connection
echo -e "\n${YELLOW}📊 Testing Database...${NC}"
if docker exec test_admin_mysql mysql -u root -proot -e "USE school_management_system_admin; SELECT COUNT(*) as admin_count FROM admin_users;" 2>/dev/null; then
    echo -e "${GREEN}✅ Database connection successful${NC}"
    
    # Check if admin user exists
    ADMIN_COUNT=$(docker exec test_admin_mysql mysql -u root -proot -e "USE school_management_system_admin; SELECT COUNT(*) FROM admin_users WHERE username='admin';" -s -N 2>/dev/null)
    if [ "$ADMIN_COUNT" -gt 0 ]; then
        echo -e "${GREEN}✅ Default admin user exists${NC}"
    else
        echo -e "${YELLOW}⚠️  Default admin user not found${NC}"
    fi
else
    echo -e "${RED}❌ Database connection failed${NC}"
fi

echo -e "\n${GREEN}✅ Authentication System Test Complete!${NC}"
echo -e "\n${BLUE}🌐 Access Points:${NC}"
echo -e "  • Admin Panel: http://localhost:8082"
echo -e "  • Admin API: http://localhost:8081/api"
echo -e "  • Database: localhost:3306"

echo -e "\n${BLUE}🔐 Login Credentials:${NC}"
echo -e "  • Username: admin"
echo -e "  • Password: admin123"

echo -e "\n${BLUE}🧪 Authentication Flow Test:${NC}"
echo -e "  1. Open http://localhost:8082 in your browser"
echo -e "  2. You should be redirected to the login page"
echo -e "  3. Enter username: admin, password: admin123"
echo -e "  4. After successful login, you should see the dashboard"
echo -e "  5. Try accessing /schools to test protected routes"
echo -e "  6. Use the logout button to test logout functionality"

echo -e "\n${YELLOW}🎯 Expected Behavior:${NC}"
echo -e "  • Unauthenticated users should be redirected to /login"
echo -e "  • Authenticated users should see the dashboard with user info"
echo -e "  • Logout should clear session and redirect to login"
echo -e "  • User info should display in the sidebar"

echo -e "\n${BLUE}📋 Useful Commands:${NC}"
echo -e "  • View logs: docker-compose -f test-admin-panel.yml logs -f"
echo -e "  • Stop services: docker-compose -f test-admin-panel.yml down"
echo -e "  • Check status: docker-compose -f test-admin-panel.yml ps"
