#!/bin/bash

# Test Admin Panel Script
# This script runs the admin panel independently for testing

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 Testing Admin Panel Independently${NC}"
echo "=============================================="

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker first.${NC}"
    exit 1
fi

# Stop any existing containers
echo -e "\n${YELLOW}🛑 Stopping existing containers...${NC}"
docker-compose -f test-admin-panel.yml down 2>/dev/null || true

# Build and start services
echo -e "\n${YELLOW}📦 Building and starting admin panel services...${NC}"
docker-compose -f test-admin-panel.yml up --build -d

# Wait for services to start
echo -e "\n${YELLOW}⏳ Waiting for services to start...${NC}"
sleep 30

# Check service status
echo -e "\n${YELLOW}🔍 Checking service status...${NC}"
docker-compose -f test-admin-panel.yml ps

# Test endpoints
echo -e "\n${YELLOW}🧪 Testing endpoints...${NC}"

# Test database
echo -e "\n${BLUE}📊 Testing Database...${NC}"
if docker exec test_admin_mysql mysql -u root -proot -e "USE school_management_system_admin; SELECT COUNT(*) FROM schools;" 2>/dev/null; then
    echo -e "${GREEN}✅ Database is working${NC}"
else
    echo -e "${RED}❌ Database connection failed${NC}"
fi

# Test backend API
echo -e "\n${BLUE}🔧 Testing Backend API...${NC}"
if curl -f http://localhost:8081/api/actuator/health >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Backend API is working${NC}"
    
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
    echo -e "${RED}❌ Backend API is not responding${NC}"
fi

# Test frontend
echo -e "\n${BLUE}🎨 Testing Frontend...${NC}"
if curl -f http://localhost:8082 >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Frontend is working${NC}"
else
    echo -e "${RED}❌ Frontend is not responding${NC}"
fi

echo -e "\n${GREEN}✅ Admin Panel Test Complete!${NC}"
echo -e "\n${BLUE}🌐 Access Points:${NC}"
echo -e "  • Admin Panel: http://localhost:8082"
echo -e "  • Admin API: http://localhost:8081/api"
echo -e "  • Database: localhost:3306"

echo -e "\n${BLUE}🔐 Login Credentials:${NC}"
echo -e "  • Username: admin"
echo -e "  • Password: password"

echo -e "\n${BLUE}📋 Test Commands:${NC}"
echo -e "  • View logs: docker-compose -f test-admin-panel.yml logs -f"
echo -e "  • Stop services: docker-compose -f test-admin-panel.yml down"
echo -e "  • Check status: docker-compose -f test-admin-panel.yml ps"

echo -e "\n${YELLOW}🎯 Next Steps:${NC}"
echo -e "  1. Open http://localhost:8082 in your browser"
echo -e "  2. Test the dashboard and schools management"
echo -e "  3. Try creating a new school"
echo -e "  4. Verify all functionality works correctly"
