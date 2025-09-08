#!/bin/bash

# Llama Model Installation Script for School Management System
# This script installs Ollama and Llama models on your server

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
OLLAMA_VERSION="latest"
LLAMA_MODEL="llama3.2:3b"  # Lightweight model for testing
LLAMA_MODEL_LARGE="llama3.2:8b"  # Larger model for production
OLLAMA_PORT="11434"
OLLAMA_USER="ollama"

echo -e "${BLUE}🚀 Installing Llama Model for School Management System${NC}"
echo "=================================================="

# Function to check if running as root
check_root() {
    if [[ $EUID -eq 0 ]]; then
        echo -e "${RED}❌ This script should not be run as root${NC}"
        echo "Please run as a regular user with sudo privileges"
        exit 1
    fi
}

# Function to detect OS
detect_os() {
    if [[ -f /etc/os-release ]]; then
        . /etc/os-release
        OS=$NAME
        VER=$VERSION_ID
    elif type lsb_release >/dev/null 2>&1; then
        OS=$(lsb_release -si)
        VER=$(lsb_release -sr)
    else
        OS=$(uname -s)
        VER=$(uname -r)
    fi
    echo -e "${BLUE}📋 Detected OS: $OS $VER${NC}"
}

# Function to install dependencies
install_dependencies() {
    echo -e "${BLUE}📦 Installing dependencies...${NC}"
    
    if command -v apt-get >/dev/null 2>&1; then
        # Ubuntu/Debian
        sudo apt-get update
        sudo apt-get install -y curl wget git build-essential
    elif command -v yum >/dev/null 2>&1; then
        # CentOS/RHEL
        sudo yum update -y
        sudo yum install -y curl wget git gcc gcc-c++ make
    elif command -v dnf >/dev/null 2>&1; then
        # Fedora
        sudo dnf update -y
        sudo dnf install -y curl wget git gcc gcc-c++ make
    else
        echo -e "${YELLOW}⚠️  Please install curl, wget, and build tools manually${NC}"
    fi
}

# Function to install Ollama
install_ollama() {
    echo -e "${BLUE}🔧 Installing Ollama...${NC}"
    
    # Check if Ollama is already installed
    if command -v ollama >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Ollama is already installed${NC}"
        ollama --version
        return 0
    fi
    
    # Download and install Ollama
    echo "Downloading Ollama installer..."
    curl -fsSL https://ollama.ai/install.sh | sh
    
    # Add ollama user if it doesn't exist
    if ! id "$OLLAMA_USER" &>/dev/null; then
        sudo useradd -r -s /bin/false -m -d /usr/share/ollama "$OLLAMA_USER"
    fi
    
    # Create systemd service
    sudo tee /etc/systemd/system/ollama.service > /dev/null <<EOF
[Unit]
Description=Ollama Service
After=network-online.target

[Service]
ExecStart=/usr/local/bin/ollama serve
User=$OLLAMA_USER
Group=$OLLAMA_USER
Restart=always
RestartSec=3
Environment="PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
Environment="OLLAMA_HOST=0.0.0.0:$OLLAMA_PORT"

[Install]
WantedBy=default.target
EOF

    # Reload systemd and start Ollama
    sudo systemctl daemon-reload
    sudo systemctl enable ollama
    sudo systemctl start ollama
    
    # Wait for Ollama to start
    echo "Waiting for Ollama to start..."
    sleep 10
    
    # Check if Ollama is running
    if sudo systemctl is-active --quiet ollama; then
        echo -e "${GREEN}✅ Ollama service started successfully${NC}"
    else
        echo -e "${RED}❌ Failed to start Ollama service${NC}"
        sudo systemctl status ollama
        exit 1
    fi
}

# Function to download Llama model
download_llama_model() {
    echo -e "${BLUE}📥 Downloading Llama model: $LLAMA_MODEL${NC}"
    echo "This may take several minutes depending on your internet connection..."
    
    # Download the model
    ollama pull $LLAMA_MODEL
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Successfully downloaded $LLAMA_MODEL${NC}"
    else
        echo -e "${RED}❌ Failed to download $LLAMA_MODEL${NC}"
        exit 1
    fi
}

# Function to download additional models (optional)
download_additional_models() {
    echo -e "${BLUE}📥 Downloading additional models...${NC}"
    
    # Download smaller models for testing
    echo "Downloading llama3.2:1b (smallest model)..."
    ollama pull llama3.2:1b
    
    # Download code-specific model
    echo "Downloading codellama:7b (for code generation)..."
    ollama pull codellama:7b
    
    echo -e "${GREEN}✅ Additional models downloaded${NC}"
}

# Function to test the installation
test_installation() {
    echo -e "${BLUE}🧪 Testing Llama installation...${NC}"
    
    # Test basic functionality
    echo "Testing basic chat..."
    response=$(ollama run $LLAMA_MODEL "Hello, can you help me with school management?" --verbose 2>/dev/null | head -5)
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Llama model is working correctly${NC}"
        echo "Sample response: $response"
    else
        echo -e "${RED}❌ Llama model test failed${NC}"
        exit 1
    fi
}

# Function to configure firewall
configure_firewall() {
    echo -e "${BLUE}🔥 Configuring firewall...${NC}"
    
    if command -v ufw >/dev/null 2>&1; then
        # Ubuntu/Debian UFW
        if ufw status | grep -q "Status: active"; then
            sudo ufw allow $OLLAMA_PORT/tcp
            echo -e "${GREEN}✅ UFW rule added for port $OLLAMA_PORT${NC}"
        fi
    elif command -v firewall-cmd >/dev/null 2>&1; then
        # CentOS/RHEL Firewalld
        if systemctl is-active --quiet firewalld; then
            sudo firewall-cmd --permanent --add-port=$OLLAMA_PORT/tcp
            sudo firewall-cmd --reload
            echo -e "${GREEN}✅ Firewalld rule added for port $OLLAMA_PORT${NC}"
        fi
    fi
}

# Function to update environment variables
update_environment() {
    echo -e "${BLUE}⚙️  Updating environment configuration...${NC}"
    
    # Update env.testing
    if [ -f "env.testing" ]; then
        sed -i "s|AI_LOCAL_ENDPOINT=.*|AI_LOCAL_ENDPOINT=http://localhost:$OLLAMA_PORT|g" env.testing
        sed -i "s|AI_LOCAL_MODEL=.*|AI_LOCAL_MODEL=$LLAMA_MODEL|g" env.testing
        echo -e "${GREEN}✅ Updated env.testing with Ollama configuration${NC}"
    fi
    
    # Update env.development
    if [ -f "env.development" ]; then
        sed -i "s|AI_LOCAL_ENDPOINT=.*|AI_LOCAL_ENDPOINT=http://localhost:$OLLAMA_PORT|g" env.development
        sed -i "s|AI_LOCAL_MODEL=.*|AI_LOCAL_MODEL=$LLAMA_MODEL|g" env.development
        echo -e "${GREEN}✅ Updated env.development with Ollama configuration${NC}"
    fi
}

# Function to show usage instructions
show_usage() {
    echo -e "\n${BLUE}📚 Usage Instructions:${NC}"
    echo "======================"
    echo ""
    echo -e "${GREEN}1. Start Ollama service:${NC}"
    echo "   sudo systemctl start ollama"
    echo ""
    echo -e "${GREEN}2. Check Ollama status:${NC}"
    echo "   sudo systemctl status ollama"
    echo ""
    echo -e "${GREEN}3. List available models:${NC}"
    echo "   ollama list"
    echo ""
    echo -e "${GREEN}4. Run a model:${NC}"
    echo "   ollama run $LLAMA_MODEL"
    echo ""
    echo -e "${GREEN}5. Test API endpoint:${NC}"
    echo "   curl http://localhost:$OLLAMA_PORT/api/generate -d '{\"model\":\"$LLAMA_MODEL\",\"prompt\":\"Hello\"}'"
    echo ""
    echo -e "${GREEN}6. Update your school management system:${NC}"
    echo "   docker-compose -f docker-compose.testing.yml up -d"
    echo ""
    echo -e "${YELLOW}📝 Note: The school management system will automatically use Ollama when AI_LOCAL_ENDPOINT is configured.${NC}"
}

# Function to show system requirements
show_requirements() {
    echo -e "\n${BLUE}💻 System Requirements:${NC}"
    echo "========================"
    echo ""
    echo -e "${GREEN}Minimum Requirements:${NC}"
    echo "• RAM: 4GB (for llama3.2:1b)"
    echo "• RAM: 8GB (for llama3.2:3b)"
    echo "• RAM: 16GB (for llama3.2:8b)"
    echo "• Storage: 2GB free space per model"
    echo "• CPU: x86_64 or ARM64"
    echo ""
    echo -e "${GREEN}Recommended Requirements:${NC}"
    echo "• RAM: 16GB+"
    echo "• Storage: 10GB+ free space"
    echo "• GPU: NVIDIA GPU with CUDA support (optional)"
    echo ""
}

# Main installation function
main() {
    echo -e "${BLUE}🎯 Starting Llama installation for School Management System${NC}"
    echo ""
    
    # Check system requirements
    show_requirements
    
    # Confirm installation
    read -p "Do you want to continue with the installation? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Installation cancelled."
        exit 0
    fi
    
    # Run installation steps
    check_root
    detect_os
    install_dependencies
    install_ollama
    download_llama_model
    
    # Ask about additional models
    read -p "Do you want to download additional models (llama3.2:1b, codellama:7b)? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        download_additional_models
    fi
    
    test_installation
    configure_firewall
    update_environment
    
    echo -e "\n${GREEN}🎉 Llama installation completed successfully!${NC}"
    echo "=================================================="
    
    show_usage
    
    echo -e "\n${YELLOW}🔧 Next Steps:${NC}"
    echo "1. Update your school management system configuration"
    echo "2. Restart your Docker containers"
    echo "3. Test AI functionality in the application"
    echo ""
    echo -e "${BLUE}📖 For more information, check the TESTING_DEPLOYMENT_GUIDE.md${NC}"
}

# Run main function
main "$@"
