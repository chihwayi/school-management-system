#!/bin/bash

# Llama Model Installation Script for School Management System (No Sudo Required)
# This script installs Ollama and Llama models without requiring sudo privileges

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
OLLAMA_DIR="$HOME/.local/bin"
OLLAMA_DATA_DIR="$HOME/.ollama"

echo -e "${BLUE}🚀 Installing Llama Model for School Management System (No Sudo)${NC}"
echo "================================================================"

# Function to check if running as root
check_root() {
    if [[ $EUID -eq 0 ]]; then
        echo -e "${RED}❌ This script should not be run as root${NC}"
        echo "Please run as a regular user"
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

# Function to create directories
create_directories() {
    echo -e "${BLUE}📁 Creating directories...${NC}"
    
    # Create local bin directory
    mkdir -p "$OLLAMA_DIR"
    mkdir -p "$OLLAMA_DATA_DIR"
    
    # Add to PATH if not already there
    if [[ ":$PATH:" != *":$OLLAMA_DIR:"* ]]; then
        echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
        export PATH="$HOME/.local/bin:$PATH"
        echo -e "${GREEN}✅ Added $OLLAMA_DIR to PATH${NC}"
    fi
}

# Function to install dependencies (user-level)
install_dependencies() {
    echo -e "${BLUE}📦 Checking dependencies...${NC}"
    
    # Check if curl is available
    if ! command -v curl >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  curl is not installed. Please install it manually:${NC}"
        echo "   Ubuntu/Debian: apt-get install curl"
        echo "   CentOS/RHEL: yum install curl"
        echo "   Or ask your system administrator to install it"
        exit 1
    fi
    
    # Check if wget is available
    if ! command -v wget >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  wget is not installed. Please install it manually:${NC}"
        echo "   Ubuntu/Debian: apt-get install wget"
        echo "   CentOS/RHEL: yum install wget"
        echo "   Or ask your system administrator to install it"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Dependencies are available${NC}"
}

# Function to install Ollama (user-level)
install_ollama() {
    echo -e "${BLUE}🔧 Installing Ollama (user-level)...${NC}"
    
    # Check if Ollama is already installed
    if command -v ollama >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Ollama is already installed${NC}"
        ollama --version
        return 0
    fi
    
    # Download Ollama binary
    echo "Downloading Ollama binary..."
    cd "$OLLAMA_DIR"
    
    # Detect architecture
    ARCH=$(uname -m)
    case $ARCH in
        x86_64) ARCH="amd64" ;;
        aarch64) ARCH="arm64" ;;
        armv7l) ARCH="arm" ;;
        *) echo -e "${RED}❌ Unsupported architecture: $ARCH${NC}"; exit 1 ;;
    esac
    
    # Download Ollama
    OLLAMA_URL="https://github.com/ollama/ollama/releases/latest/download/ollama-linux-${ARCH}"
    curl -L "$OLLAMA_URL" -o ollama
    chmod +x ollama
    
    echo -e "${GREEN}✅ Ollama installed successfully${NC}"
}

# Function to create user service script
create_service_script() {
    echo -e "${BLUE}🔧 Creating Ollama service script...${NC}"
    
    # Create service script
    cat > "$HOME/start-ollama.sh" <<EOF
#!/bin/bash
# Ollama Service Script (User-level)

export OLLAMA_HOST=0.0.0.0:$OLLAMA_PORT
export OLLAMA_DATA_DIR="$OLLAMA_DATA_DIR"

echo "Starting Ollama service..."
echo "Host: \$OLLAMA_HOST"
echo "Data Directory: \$OLLAMA_DATA_DIR"

# Start Ollama
$OLLAMA_DIR/ollama serve
EOF

    chmod +x "$HOME/start-ollama.sh"
    echo -e "${GREEN}✅ Service script created: $HOME/start-ollama.sh${NC}"
}

# Function to download Llama model
download_llama_model() {
    echo -e "${BLUE}📥 Downloading Llama model: $LLAMA_MODEL${NC}"
    echo "This may take several minutes depending on your internet connection..."
    
    # Start Ollama in background
    echo "Starting Ollama service..."
    export OLLAMA_HOST=0.0.0.0:$OLLAMA_PORT
    export OLLAMA_DATA_DIR="$OLLAMA_DATA_DIR"
    
    # Start Ollama in background
    nohup "$OLLAMA_DIR/ollama" serve > "$HOME/ollama.log" 2>&1 &
    OLLAMA_PID=$!
    
    # Wait for Ollama to start
    echo "Waiting for Ollama to start..."
    sleep 10
    
    # Check if Ollama is running
    if curl -s http://localhost:$OLLAMA_PORT/api/tags >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Ollama service started successfully${NC}"
    else
        echo -e "${RED}❌ Failed to start Ollama service${NC}"
        echo "Check the log file: $HOME/ollama.log"
        exit 1
    fi
    
    # Download the model
    "$OLLAMA_DIR/ollama" pull $LLAMA_MODEL
    
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
    "$OLLAMA_DIR/ollama" pull llama3.2:1b
    
    echo -e "${GREEN}✅ Additional models downloaded${NC}"
}

# Function to test the installation
test_installation() {
    echo -e "${BLUE}🧪 Testing Llama installation...${NC}"
    
    # Test basic functionality
    echo "Testing basic chat..."
    response=$("$OLLAMA_DIR/ollama" run $LLAMA_MODEL "Hello, can you help me with school management?" --verbose 2>/dev/null | head -5)
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Llama model is working correctly${NC}"
        echo "Sample response: $response"
    else
        echo -e "${RED}❌ Llama model test failed${NC}"
        exit 1
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

# Function to create startup script
create_startup_script() {
    echo -e "${BLUE}🔧 Creating startup script...${NC}"
    
    cat > "$HOME/start-ollama-service.sh" <<EOF
#!/bin/bash
# Start Ollama Service Script

# Set environment variables
export OLLAMA_HOST=0.0.0.0:$OLLAMA_PORT
export OLLAMA_DATA_DIR="$OLLAMA_DATA_DIR"
export PATH="$HOME/.local/bin:\$PATH"

# Check if Ollama is already running
if pgrep -f "ollama serve" > /dev/null; then
    echo "Ollama is already running"
    exit 0
fi

# Start Ollama
echo "Starting Ollama service..."
echo "Host: \$OLLAMA_HOST"
echo "Data Directory: \$OLLAMA_DATA_DIR"

nohup $OLLAMA_DIR/ollama serve > "$HOME/ollama.log" 2>&1 &
echo \$! > "$HOME/ollama.pid"

echo "Ollama started with PID: \$(cat $HOME/ollama.pid)"
echo "Log file: $HOME/ollama.log"
EOF

    chmod +x "$HOME/start-ollama-service.sh"
    echo -e "${GREEN}✅ Startup script created: $HOME/start-ollama-service.sh${NC}"
}

# Function to create stop script
create_stop_script() {
    echo -e "${BLUE}🔧 Creating stop script...${NC}"
    
    cat > "$HOME/stop-ollama-service.sh" <<EOF
#!/bin/bash
# Stop Ollama Service Script

if [ -f "$HOME/ollama.pid" ]; then
    PID=\$(cat "$HOME/ollama.pid")
    if kill -0 \$PID 2>/dev/null; then
        kill \$PID
        echo "Ollama service stopped (PID: \$PID)"
        rm -f "$HOME/ollama.pid"
    else
        echo "Ollama service is not running"
        rm -f "$HOME/ollama.pid"
    fi
else
    echo "No PID file found. Stopping all Ollama processes..."
    pkill -f "ollama serve"
fi
EOF

    chmod +x "$HOME/stop-ollama-service.sh"
    echo -e "${GREEN}✅ Stop script created: $HOME/stop-ollama-service.sh${NC}"
}

# Function to show usage instructions
show_usage() {
    echo -e "\n${BLUE}📚 Usage Instructions:${NC}"
    echo "======================"
    echo ""
    echo -e "${GREEN}1. Start Ollama service:${NC}"
    echo "   $HOME/start-ollama-service.sh"
    echo ""
    echo -e "${GREEN}2. Stop Ollama service:${NC}"
    echo "   $HOME/stop-ollama-service.sh"
    echo ""
    echo -e "${GREEN}3. Check Ollama status:${NC}"
    echo "   curl http://localhost:$OLLAMA_PORT/api/tags"
    echo ""
    echo -e "${GREEN}4. List available models:${NC}"
    echo "   $OLLAMA_DIR/ollama list"
    echo ""
    echo -e "${GREEN}5. Run a model:${NC}"
    echo "   $OLLAMA_DIR/ollama run $LLAMA_MODEL"
    echo ""
    echo -e "${GREEN}6. Test API endpoint:${NC}"
    echo "   curl http://localhost:$OLLAMA_PORT/api/generate -d '{\"model\":\"$LLAMA_MODEL\",\"prompt\":\"Hello\"}'"
    echo ""
    echo -e "${GREEN}7. Update your school management system:${NC}"
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
    echo -e "${BLUE}🎯 Starting Llama installation for School Management System (No Sudo)${NC}"
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
    create_directories
    install_dependencies
    install_ollama
    create_service_script
    download_llama_model
    
    # Ask about additional models
    read -p "Do you want to download additional models (llama3.2:1b)? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        download_additional_models
    fi
    
    test_installation
    update_environment
    create_startup_script
    create_stop_script
    
    echo -e "\n${GREEN}🎉 Llama installation completed successfully!${NC}"
    echo "=================================================="
    
    show_usage
    
    echo -e "\n${YELLOW}🔧 Next Steps:${NC}"
    echo "1. Start Ollama service: $HOME/start-ollama-service.sh"
    echo "2. Update your school management system configuration"
    echo "3. Restart your Docker containers"
    echo "4. Test AI functionality in the application"
    echo ""
    echo -e "${BLUE}📖 For more information, check the LLAMA_INSTALLATION_GUIDE.md${NC}"
}

# Run main function
main "$@"
