#!/bin/bash

# School Management System Port Checker
# This script checks if all required ports are available and open on the server

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Ports used by the school management system
declare -A PORTS
PORTS["MySQL Database"]="3306"
PORTS["Spring Boot Backend"]="8080"
PORTS["React Frontend (Dev)"]="3000"
PORTS["React Frontend (Prod)"]="80"
PORTS["Nginx Reverse Proxy HTTP"]="80"
PORTS["Nginx Reverse Proxy HTTPS"]="443"
PORTS["Java Debug Port (Dev)"]="5005"

# Function to check if a port is in use
check_port_in_use() {
    local port=$1
    if lsof -i :$port >/dev/null 2>&1; then
        return 0  # Port is in use
    else
        return 1  # Port is free
    fi
}

# Function to check if a port is listening
check_port_listening() {
    local port=$1
    if netstat -tuln | grep -q ":$port "; then
        return 0  # Port is listening
    else
        return 1  # Port is not listening
    fi
}

# Function to test port connectivity
test_port_connectivity() {
    local port=$1
    local host=${2:-"localhost"}
    
    if timeout 3 bash -c "</dev/tcp/$host/$port" 2>/dev/null; then
        return 0  # Port is reachable
    else
        return 1  # Port is not reachable
    fi
}

# Function to get process using the port
get_port_process() {
    local port=$1
    lsof -i :$port 2>/dev/null | tail -n +2 | awk '{print $1, $2}' | head -1
}

# Function to check firewall status
check_firewall() {
    echo -e "\n${BLUE}=== Firewall Status ===${NC}"
    
    # Check if ufw is active (Ubuntu/Debian)
    if command -v ufw >/dev/null 2>&1; then
        if ufw status | grep -q "Status: active"; then
            echo -e "${YELLOW}UFW Firewall is ACTIVE${NC}"
            echo "UFW Status:"
            ufw status numbered
        else
            echo -e "${GREEN}UFW Firewall is INACTIVE${NC}"
        fi
    fi
    
    # Check if firewalld is active (CentOS/RHEL)
    if command -v firewall-cmd >/dev/null 2>&1; then
        if systemctl is-active --quiet firewalld; then
            echo -e "${YELLOW}Firewalld is ACTIVE${NC}"
            echo "Firewalld Status:"
            firewall-cmd --list-all
        else
            echo -e "${GREEN}Firewalld is INACTIVE${NC}"
        fi
    fi
    
    # Check iptables
    if command -v iptables >/dev/null 2>&1; then
        echo -e "\n${BLUE}IPTables Rules:${NC}"
        iptables -L -n | grep -E "(ACCEPT|DROP|REJECT)" | head -10
    fi
}

# Function to check Docker status
check_docker() {
    echo -e "\n${BLUE}=== Docker Status ===${NC}"
    
    if command -v docker >/dev/null 2>&1; then
        if systemctl is-active --quiet docker 2>/dev/null || docker info >/dev/null 2>&1; then
            echo -e "${GREEN}Docker is RUNNING${NC}"
            echo "Docker containers using ports:"
            docker ps --format "table {{.Names}}\t{{.Ports}}" | grep -E ":(3306|8080|3000|80|443|5005)"
        else
            echo -e "${RED}Docker is NOT RUNNING${NC}"
        fi
    else
        echo -e "${YELLOW}Docker is NOT INSTALLED${NC}"
    fi
}

# Main port checking function
check_ports() {
    echo -e "${BLUE}=== School Management System Port Check ===${NC}"
    echo -e "Checking ports on: $(hostname) ($(date))"
    echo ""
    
    local all_ports_ok=true
    
    for service in "${!PORTS[@]}"; do
        local port=${PORTS[$service]}
        echo -e "${BLUE}Checking $service (Port $port):${NC}"
        
        # Check if port is in use
        if check_port_in_use $port; then
            local process=$(get_port_process $port)
            echo -e "  ${YELLOW}⚠️  Port $port is IN USE by: $process${NC}"
            
            # Check if it's listening
            if check_port_listening $port; then
                echo -e "  ${GREEN}✅ Port $port is LISTENING${NC}"
                
                # Test connectivity
                if test_port_connectivity $port; then
                    echo -e "  ${GREEN}✅ Port $port is REACHABLE${NC}"
                else
                    echo -e "  ${RED}❌ Port $port is NOT REACHABLE${NC}"
                    all_ports_ok=false
                fi
            else
                echo -e "  ${RED}❌ Port $port is NOT LISTENING${NC}"
                all_ports_ok=false
            fi
        else
            echo -e "  ${GREEN}✅ Port $port is AVAILABLE${NC}"
        fi
        echo ""
    done
    
    return $all_ports_ok
}

# Function to provide recommendations
provide_recommendations() {
    echo -e "\n${BLUE}=== Recommendations ===${NC}"
    
    echo -e "${YELLOW}For Development Environment:${NC}"
    echo "• Ports 3000, 8080, 3306, and 5005 should be available"
    echo "• Use: docker-compose -f docker-compose.dev.yml up"
    
    echo -e "\n${YELLOW}For Production Environment:${NC}"
    echo "• Only ports 80 and 443 should be exposed to the internet"
    echo "• Ports 8080 and 3306 should be internal only"
    echo "• Use: docker-compose -f docker-compose.prod.yml up"
    
    echo -e "\n${YELLOW}Security Recommendations:${NC}"
    echo "• Never expose MySQL (3306) to the internet in production"
    echo "• Use a reverse proxy (Nginx) for production"
    echo "• Enable SSL/TLS on port 443"
    echo "• Configure firewall to only allow necessary ports"
    
    echo -e "\n${YELLOW}If ports are in use:${NC}"
    echo "• Check what's using them: sudo lsof -i :PORT"
    echo "• Stop conflicting services: sudo systemctl stop SERVICE"
    echo "• Kill processes: sudo kill -9 PID"
    echo "• Change ports in docker-compose files if needed"
}

# Function to show current system info
show_system_info() {
    echo -e "${BLUE}=== System Information ===${NC}"
    echo "Hostname: $(hostname)"
    echo "OS: $(uname -s) $(uname -r)"
    echo "Architecture: $(uname -m)"
    echo "Uptime: $(uptime)"
    echo "Load Average: $(uptime | awk -F'load average:' '{print $2}')"
    if command -v free >/dev/null 2>&1; then
        echo "Memory: $(free -h | awk '/^Mem:/ {print $3 "/" $2}')"
    else
        echo "Memory: $(vm_stat | grep -E 'Pages (free|active|inactive|speculative|wired)' | awk '{sum+=$3} END {print sum*4096/1024/1024/1024 " GB"}')"
    fi
    echo "Disk Usage: $(df -h / | awk 'NR==2 {print $3 "/" $2 " (" $5 " used)"}')"
    echo ""
}

# Main execution
main() {
    show_system_info
    
    if check_ports; then
        echo -e "${GREEN}🎉 All required ports are available and working!${NC}"
        exit 0
    else
        echo -e "${RED}⚠️  Some ports have issues. Check the details above.${NC}"
        exit 1
    fi
    
    check_docker
    check_firewall
    provide_recommendations
    
    echo -e "\n${BLUE}=== Quick Commands ===${NC}"
    echo "Check specific port: sudo lsof -i :PORT"
    echo "Check all listening ports: sudo netstat -tuln"
    echo "Test port connectivity: telnet localhost PORT"
    echo "Start development: docker-compose -f docker-compose.dev.yml up"
    echo "Start production: docker-compose -f docker-compose.prod.yml up"
}

# Run the main function
main "$@"
