# School Management System - Server Deployment Guide

## Recommended Directory Structure

### Option 1: `/opt` (Recommended for Production)
```bash
/opt/school-management-system/
├── school-management-frontend/
├── school-management-system/
├── scripts/
├── docker-compose.yml
├── docker-compose.prod.yml
├── docker-compose.dev.yml
└── SERVER_PORT_CHECK_GUIDE.md
```

**Pros:**
- ✅ Standard location for third-party applications
- ✅ Clean separation from system files
- ✅ Easy to manage permissions
- ✅ Good for production deployments

### Option 2: `/home/user` (Good for Development/Testing)
```bash
/home/your-username/school-management-system/
```

**Pros:**
- ✅ User-owned, no sudo needed for most operations
- ✅ Good for development and testing
- ✅ Easy backup and management

### Option 3: `/var/www` (Web Application Standard)
```bash
/var/www/school-management-system/
```

**Pros:**
- ✅ Standard for web applications
- ✅ Often used with web servers
- ✅ Good for production web apps

## Step-by-Step Deployment Instructions

### 1. Connect to Your Server
```bash
ssh your-username@your-server-ip
```

### 2. Create the Directory (Recommended: /opt)
```bash
# Create directory with proper permissions
sudo mkdir -p /opt/school-management-system
sudo chown $USER:$USER /opt/school-management-system
```

### 3. Clone the Repository
```bash
cd /opt/school-management-system
git clone https://github.com/chihwayi/school-management-system.git .
```

### 4. Set Up Permissions
```bash
# Make scripts executable
chmod +x scripts/*.sh

# Set proper ownership
sudo chown -R $USER:$USER /opt/school-management-system
```

### 5. Check Ports Before Deployment
```bash
# Run the port checker
./scripts/check-ports.sh
```

### 6. Set Up Environment Variables
```bash
# Copy environment files
cp env.production .env.production
cp env.development .env.development

# Edit with your actual values
nano .env.production
```

### 7. Deploy Based on Environment

#### For Development:
```bash
docker-compose -f docker-compose.dev.yml up -d
```

#### For Production:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Complete Deployment Script

Here's a complete script you can run on your server:

```bash
#!/bin/bash
# School Management System Deployment Script

set -e

# Configuration
PROJECT_DIR="/opt/school-management-system"
REPO_URL="https://github.com/chihwayi/school-management-system.git"
USER=$(whoami)

echo "🚀 Starting School Management System Deployment..."

# 1. Create directory
echo "📁 Creating project directory..."
sudo mkdir -p $PROJECT_DIR
sudo chown $USER:$USER $PROJECT_DIR

# 2. Clone repository
echo "📥 Cloning repository..."
cd $PROJECT_DIR
if [ -d ".git" ]; then
    echo "Repository already exists, pulling latest changes..."
    git pull origin main
else
    git clone $REPO_URL .
fi

# 3. Set permissions
echo "🔐 Setting permissions..."
chmod +x scripts/*.sh
sudo chown -R $USER:$USER $PROJECT_DIR

# 4. Check ports
echo "🔍 Checking port availability..."
./scripts/check-ports.sh

# 5. Set up environment
echo "⚙️ Setting up environment..."
if [ ! -f ".env.production" ]; then
    cp env.production .env.production
    echo "⚠️  Please edit .env.production with your actual values"
fi

# 6. Deploy
echo "🐳 Starting Docker containers..."
read -p "Deploy for production? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker-compose -f docker-compose.prod.yml up -d
    echo "✅ Production deployment complete!"
    echo "🌐 Access your application at: http://your-server-ip"
else
    docker-compose -f docker-compose.dev.yml up -d
    echo "✅ Development deployment complete!"
    echo "🌐 Access your application at: http://your-server-ip:3000"
fi

echo "🎉 Deployment finished!"
```

## Directory Structure After Deployment

```
/opt/school-management-system/
├── school-management-frontend/          # React frontend
├── school-management-system/            # Spring Boot backend
├── scripts/                            # Deployment scripts
│   ├── check-ports.sh                  # Port checker
│   ├── backup-db.sh                    # Database backup
│   ├── deploy-prod.sh                  # Production deploy
│   └── ...
├── mysql/                              # Database initialization
├── docker-compose.yml                  # Default compose
├── docker-compose.dev.yml              # Development compose
├── docker-compose.prod.yml             # Production compose
├── .env.production                     # Production environment
├── .env.development                    # Development environment
├── SERVER_PORT_CHECK_GUIDE.md          # Port checking guide
└── README.md                           # Project documentation
```

## Post-Deployment Checklist

### ✅ Verify Deployment
```bash
# Check if containers are running
docker ps

# Check application health
curl http://localhost:3000  # Development
curl http://localhost:80    # Production

# Check logs
docker-compose logs -f
```

### ✅ Configure Firewall
```bash
# For production
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# For development
sudo ufw allow 3000/tcp
sudo ufw allow 8080/tcp
```

### ✅ Set Up SSL (Production)
```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx

# Get SSL certificate
sudo certbot --nginx -d yourdomain.com
```

### ✅ Configure Reverse Proxy (Production)
```bash
# Create Nginx configuration
sudo nano /etc/nginx/sites-available/school-management

# Enable site
sudo ln -s /etc/nginx/sites-available/school-management /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## Backup and Maintenance

### Database Backup
```bash
# Run backup script
./scripts/backup-db.sh

# Manual backup
docker exec school_mysql_prod mysqldump -u root -p school_management_system > backup.sql
```

### Update Application
```bash
# Pull latest changes
git pull origin main

# Rebuild and restart
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d --build
```

## Security Considerations

### ✅ File Permissions
```bash
# Set proper permissions
sudo chown -R www-data:www-data /opt/school-management-system
sudo chmod -R 755 /opt/school-management-system
sudo chmod 600 .env.production  # Protect environment files
```

### ✅ Database Security
```bash
# Change default passwords
# Use strong passwords in .env.production
# Restrict database access to localhost only
```

### ✅ Firewall Rules
```bash
# Only allow necessary ports
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

## Troubleshooting

### Common Issues:
1. **Port conflicts**: Run `./scripts/check-ports.sh`
2. **Permission issues**: Check file ownership and permissions
3. **Docker issues**: Check Docker daemon status
4. **Database connection**: Verify MySQL is running and accessible

### Useful Commands:
```bash
# Check container status
docker ps -a

# View logs
docker-compose logs -f [service_name]

# Restart services
docker-compose restart

# Clean up
docker system prune -a
```

## Recommended: /opt/school-management-system

**Why /opt is the best choice:**
- ✅ Standard location for applications
- ✅ Clean separation from system files
- ✅ Easy to manage and backup
- ✅ Good for production deployments
- ✅ Follows Linux filesystem hierarchy standards

Use the deployment script above for a smooth setup process!
