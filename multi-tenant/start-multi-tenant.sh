#!/bin/bash

# Multi-Tenant School Management System Startup Script
# This script starts the complete multi-tenant system

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting Multi-Tenant School Management System${NC}"
echo "======================================================"

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker first.${NC}"
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ docker-compose is not installed. Please install it first.${NC}"
    exit 1
fi

# Navigate to deployment directory
cd "$(dirname "$0")/deployment"

echo -e "\n${YELLOW}📦 Building Docker images...${NC}"
docker-compose build --no-cache

echo -e "\n${YELLOW}🚀 Starting services...${NC}"
docker-compose up -d

echo -e "\n${YELLOW}⏳ Waiting for services to start...${NC}"
sleep 30

echo -e "\n${YELLOW}🔍 Checking service status...${NC}"
docker-compose ps

echo -e "\n${GREEN}✅ Multi-tenant system is starting up!${NC}"
echo -e "\n${BLUE}🌐 Access Points:${NC}"
echo -e "  • Admin Panel: http://localhost:8082"
echo -e "  • Admin API: http://localhost:8081/api"
echo -e "  • Main Proxy: http://localhost:80"
echo -e "  • Database: localhost:3306"

echo -e "\n${BLUE}📋 Service Ports:${NC}"
echo -e "  • MySQL Database: 3306"
echo -e "  • Admin Backend: 8081"
echo -e "  • Admin Frontend: 8082"
echo -e "  • Nginx Proxy: 80"

echo -e "\n${YELLOW}⏳ Services are starting up. Please wait 2-3 minutes for all services to be ready.${NC}"
echo -e "\n${BLUE}📚 Next Steps:${NC}"
echo -e "  1. Wait for all services to be healthy"
echo -e "  2. Access the admin panel at http://localhost:8082"
echo -e "  3. Run the test script: ./test-multi-tenant.sh"
echo -e "  4. Create your first school using the admin interface"

echo -e "\n${YELLOW}📊 To monitor services:${NC}"
echo -e "  • View logs: docker-compose logs -f"
echo -e "  • Check status: docker-compose ps"
echo -e "  • Stop services: docker-compose down"

echo -e "\n${GREEN}🎉 Multi-tenant system startup initiated!${NC}"


