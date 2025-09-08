# Llama Model Installation Guide for School Management System

This guide will help you install and configure Llama models on your server for local AI functionality in your school management system.

## 🎯 Overview

The school management system supports local AI models through Ollama, which allows you to run Llama models locally without depending on external AI services. This is perfect for:
- ✅ **Privacy**: All data stays on your server
- ✅ **Cost-effective**: No API costs
- ✅ **Offline capability**: Works without internet
- ✅ **Customization**: Fine-tune models for your needs

## 🚀 Quick Installation

### Option 1: Automated Installation (Recommended)
```bash
# Upload and run the installation script
scp scripts/install-llama.sh user@your-server:/home/user/
ssh user@your-server
chmod +x install-llama.sh
./install-llama.sh
```

### Option 2: Manual Installation
Follow the step-by-step instructions below.

## 📋 System Requirements

### Minimum Requirements:
- **RAM**: 4GB (for llama3.2:1b)
- **RAM**: 8GB (for llama3.2:3b) 
- **RAM**: 16GB (for llama3.2:8b)
- **Storage**: 2GB free space per model
- **CPU**: x86_64 or ARM64

### Recommended Requirements:
- **RAM**: 16GB+
- **Storage**: 10GB+ free space
- **GPU**: NVIDIA GPU with CUDA support (optional)

## 🔧 Step-by-Step Installation

### 1. Install Dependencies
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y curl wget git build-essential

# CentOS/RHEL
sudo yum update -y
sudo yum install -y curl wget git gcc gcc-c++ make

# Fedora
sudo dnf update -y
sudo dnf install -y curl wget git gcc gcc-c++ make
```

### 2. Install Ollama
```bash
# Download and install Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Create ollama user
sudo useradd -r -s /bin/false -m -d /usr/share/ollama ollama
```

### 3. Create Systemd Service
```bash
# Create Ollama service file
sudo tee /etc/systemd/system/ollama.service > /dev/null <<EOF
[Unit]
Description=Ollama Service
After=network-online.target

[Service]
ExecStart=/usr/local/bin/ollama serve
User=ollama
Group=ollama
Restart=always
RestartSec=3
Environment="PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
Environment="OLLAMA_HOST=0.0.0.0:11434"

[Install]
WantedBy=default.target
EOF

# Enable and start Ollama
sudo systemctl daemon-reload
sudo systemctl enable ollama
sudo systemctl start ollama
```

### 4. Download Llama Models
```bash
# Download lightweight model for testing
ollama pull llama3.2:3b

# Download smallest model (if you have limited RAM)
ollama pull llama3.2:1b

# Download larger model (if you have enough RAM)
ollama pull llama3.2:8b

# Download code-specific model
ollama pull codellama:7b
```

### 5. Configure Firewall
```bash
# Ubuntu/Debian (UFW)
sudo ufw allow 11434/tcp

# CentOS/RHEL (Firewalld)
sudo firewall-cmd --permanent --add-port=11434/tcp
sudo firewall-cmd --reload
```

### 6. Test Installation
```bash
# Test basic functionality
ollama run llama3.2:3b "Hello, can you help me with school management?"

# Test API endpoint
curl http://localhost:11434/api/generate -d '{"model":"llama3.2:3b","prompt":"Hello"}'
```

## ⚙️ Configuration

### Update Environment Variables
```bash
# Update env.testing
sed -i 's|AI_LOCAL_ENDPOINT=.*|AI_LOCAL_ENDPOINT=http://localhost:11434|g' env.testing
sed -i 's|AI_LOCAL_MODEL=.*|AI_LOCAL_MODEL=llama3.2:3b|g' env.testing

# Update env.development
sed -i 's|AI_LOCAL_ENDPOINT=.*|AI_LOCAL_ENDPOINT=http://localhost:11434|g' env.development
sed -i 's|AI_LOCAL_MODEL=.*|AI_LOCAL_MODEL=llama3.2:3b|g' env.development
```

### Update Docker Compose
```bash
# Add Ollama service to docker-compose.testing.yml
cat >> docker-compose.testing.yml <<EOF

  # Ollama for Local AI
  ollama:
    image: ollama/ollama:latest
    container_name: school_ollama_testing
    restart: unless-stopped
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    networks:
      - school_testing_network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:11434/api/tags"]
      interval: 30s
      timeout: 10s
      retries: 3
    deploy:
      resources:
        reservations:
          devices:
            - driver: nvidia
              count: 1
              capabilities: [gpu]
EOF
```

## 🧪 Testing the Installation

### 1. Check Ollama Status
```bash
# Check service status
sudo systemctl status ollama

# Check if Ollama is responding
curl http://localhost:11434/api/tags
```

### 2. List Available Models
```bash
# List downloaded models
ollama list

# Expected output:
# NAME            ID              SIZE    MODIFIED
# llama3.2:3b     abc123...       2.0 GB  2 hours ago
```

### 3. Test Model Response
```bash
# Test basic chat
ollama run llama3.2:3b "What is a school management system?"

# Test API endpoint
curl -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.2:3b",
    "prompt": "Explain school management in one sentence",
    "stream": false
  }'
```

### 4. Test with School Management System
```bash
# Restart your application
docker-compose -f docker-compose.testing.yml down
docker-compose -f docker-compose.testing.yml up -d

# Check logs for AI integration
docker-compose -f docker-compose.testing.yml logs backend | grep -i "ai\|llama\|ollama"
```

## 📊 Model Comparison

| Model | Size | RAM Required | Speed | Quality | Use Case |
|-------|------|--------------|-------|---------|----------|
| llama3.2:1b | 1.3GB | 4GB | Fast | Basic | Testing, simple tasks |
| llama3.2:3b | 2.0GB | 8GB | Good | Good | General use, testing |
| llama3.2:8b | 4.7GB | 16GB | Slower | High | Production, complex tasks |
| codellama:7b | 3.8GB | 12GB | Good | High | Code generation, technical |

## 🔧 Advanced Configuration

### GPU Support (Optional)
```bash
# Install NVIDIA drivers (Ubuntu)
sudo apt-get install -y nvidia-driver-535

# Install NVIDIA Container Toolkit
distribution=$(. /etc/os-release;echo $ID$VERSION_ID)
curl -s -L https://nvidia.github.io/nvidia-docker/gpgkey | sudo apt-key add -
curl -s -L https://nvidia.github.io/nvidia-docker/$distribution/nvidia-docker.list | sudo tee /etc/apt/sources.list.d/nvidia-docker.list

sudo apt-get update && sudo apt-get install -y nvidia-docker2
sudo systemctl restart docker
```

### Custom Model Configuration
```bash
# Create custom model file
cat > Modelfile <<EOF
FROM llama3.2:3b

# Set system prompt for school management
SYSTEM "You are an AI assistant for a school management system. Help users with educational tasks, student management, and school operations."

# Set parameters
PARAMETER temperature 0.7
PARAMETER top_p 0.9
PARAMETER top_k 40
EOF

# Create custom model
ollama create school-ai -f Modelfile
```

### Performance Optimization
```bash
# Set environment variables for better performance
export OLLAMA_NUM_PARALLEL=2
export OLLAMA_MAX_LOADED_MODELS=2
export OLLAMA_FLASH_ATTENTION=1

# Restart Ollama service
sudo systemctl restart ollama
```

## 🚨 Troubleshooting

### Common Issues

#### 1. Ollama Service Won't Start
```bash
# Check service status
sudo systemctl status ollama

# Check logs
sudo journalctl -u ollama -f

# Restart service
sudo systemctl restart ollama
```

#### 2. Model Download Fails
```bash
# Check internet connection
ping ollama.ai

# Check disk space
df -h

# Retry download
ollama pull llama3.2:3b
```

#### 3. Out of Memory Errors
```bash
# Check available memory
free -h

# Use smaller model
ollama pull llama3.2:1b

# Set memory limits
export OLLAMA_MAX_LOADED_MODELS=1
```

#### 4. API Not Responding
```bash
# Check if Ollama is listening
netstat -tuln | grep 11434

# Check firewall
sudo ufw status
sudo firewall-cmd --list-ports

# Test local connection
curl http://localhost:11434/api/tags
```

### Performance Issues

#### Slow Response Times
```bash
# Use smaller model
ollama pull llama3.2:1b

# Reduce context length
export OLLAMA_MAX_CONTEXT=2048

# Use GPU acceleration (if available)
nvidia-smi
```

#### High Memory Usage
```bash
# Monitor memory usage
htop

# Unload unused models
ollama stop llama3.2:8b

# Set memory limits
export OLLAMA_MAX_LOADED_MODELS=1
```

## 📈 Monitoring

### Check Ollama Status
```bash
# Service status
sudo systemctl status ollama

# API health
curl http://localhost:11434/api/tags

# Model status
ollama list
```

### Monitor Resources
```bash
# Memory usage
free -h

# CPU usage
top

# Disk usage
df -h

# GPU usage (if available)
nvidia-smi
```

## 🔄 Updates and Maintenance

### Update Ollama
```bash
# Stop service
sudo systemctl stop ollama

# Update Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Start service
sudo systemctl start ollama
```

### Update Models
```bash
# Update specific model
ollama pull llama3.2:3b

# Update all models
ollama list | grep -v "NAME" | awk '{print $1}' | xargs -I {} ollama pull {}
```

### Backup Models
```bash
# Backup model data
sudo tar -czf ollama-backup.tar.gz /usr/share/ollama/.ollama

# Restore models
sudo tar -xzf ollama-backup.tar.gz -C /
```

## 🎉 Integration with School Management System

Once Ollama is installed and configured:

1. **Update environment variables** with Ollama endpoint
2. **Restart Docker containers** to pick up new configuration
3. **Test AI functionality** in the application
4. **Configure AI provider** to use local Ollama instead of external APIs

The school management system will automatically detect and use Ollama when `AI_LOCAL_ENDPOINT` is configured in your environment variables.

## 📞 Support

If you encounter issues:

1. **Check the logs**: `sudo journalctl -u ollama -f`
2. **Verify configuration**: Check environment variables
3. **Test connectivity**: `curl http://localhost:11434/api/tags`
4. **Check resources**: Ensure sufficient RAM and disk space
5. **Review troubleshooting section** above

For additional help, check the Ollama documentation: https://ollama.ai/docs
