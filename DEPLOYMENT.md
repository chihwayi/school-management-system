# 🚀 School Management System - Production Deployment Guide

This guide provides step-by-step instructions for deploying your School Management System to a production server.

## 📋 Prerequisites

### Server Requirements
- **OS**: Ubuntu 20.04+ or CentOS 8+ (recommended)
- **RAM**: Minimum 4GB, recommended 8GB+
- **Storage**: Minimum 20GB free space
- **CPU**: 2+ cores recommended
- **Network**: Public IP with ports 80, 443 accessible

### Software Requirements
- Docker Engine 20.10+
- Docker Compose 2.0+
- Git
- Nginx (for reverse proxy and SSL)

## 🔧 Server Setup

### 1. Install Docker and Docker Compose

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Logout and login again to apply docker group changes
```

### 2. Clone Your Repository

```bash
# Clone your repository
git clone <your-repository-url> /opt/school-management
cd /opt/school-management

# Make scripts executable
chmod +x scripts/*.sh
```

## 🔐 Environment Configuration

### 1. Configure Production Environment

Copy and edit the production environment file:

```bash
# Copy the template
cp env.production .env.production

# Edit with your secure values
nano .env.production
```

**IMPORTANT**: Change these values in `.env.production`:

```bash
# Database passwords (CHANGE THESE!)
MYSQL_ROOT_PASSWORD=your_very_secure_root_password_here
MYSQL_PASSWORD=your_secure_db_password_here

# JWT secret (CHANGE THIS!)
JWT_SECRET=your_very_long_jwt_secret_key_here_minimum_256_bits

# AI API keys (if using AI features)
AI_OPENAI_API_KEY=your_openai_api_key_here
AI_ANTHROPIC_API_KEY=your_anthropic_api_key_here

# Domain configuration
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

### 2. Update Frontend API URL

Edit `docker-compose.prod.yml` and update the frontend build args:

```yaml
frontend:
  build:
    context: ./school-management-frontend
    dockerfile: Dockerfile
    args:
      - VITE_API_BASE_URL=https://yourdomain.com/api  # Change this
```

## 🌐 Nginx Reverse Proxy Setup

### 1. Install Nginx

```bash
sudo apt install nginx -y
sudo systemctl enable nginx
```

### 2. Create Nginx Configuration

Create `/etc/nginx/sites-available/school-management`:

```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;
    
    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    
    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
    
    # Frontend
    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # Backend API
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Increase timeout for file uploads
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # File uploads
    location /uploads/ {
        proxy_pass http://localhost:8080/uploads/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 3. Enable the Site

```bash
sudo ln -s /etc/nginx/sites-available/school-management /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## 🔒 SSL Certificate Setup

### Install Certbot and Get SSL Certificate

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Get SSL certificate (replace with your domain)
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Test auto-renewal
sudo certbot renew --dry-run
```

## 🚀 Deployment Process

### 1. Deploy with Script

```bash
# Run the production deployment script
./scripts/deploy-prod.sh
```

### 2. Manual Deployment

```bash
# Build and start production services
docker-compose -f docker-compose.prod.yml up --build -d

# Check service status
docker-compose -f docker-compose.prod.yml ps

# View logs
docker-compose -f docker-compose.prod.yml logs -f
```

## 📊 Monitoring and Maintenance

### 1. System Monitoring

```bash
# Run system monitoring script
./scripts/monitor.sh

# View service logs
docker-compose -f docker-compose.prod.yml logs -f

# Check service health
docker-compose -f docker-compose.prod.yml ps
```

### 2. Database Backup

```bash
# Create manual backup
./scripts/backup-db.sh

# Set up automated backups (add to crontab)
crontab -e

# Add this line for daily backups at 2 AM
0 2 * * * cd /opt/school-management && ./scripts/backup-db.sh
```

### 3. Update Process

```bash
# Pull latest changes
git pull origin main

# Rebuild and restart services
docker-compose -f docker-compose.prod.yml up --build -d

# Or use the deployment script
./scripts/deploy-prod.sh
```

## 🛡️ Security Best Practices

### 1. Firewall Configuration

```bash
# Install UFW
sudo apt install ufw -y

# Configure firewall
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### 2. Regular Updates

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Update Docker images
docker-compose -f docker-compose.prod.yml pull
docker-compose -f docker-compose.prod.yml up -d
```

## 📝 Quick Commands Reference

### Using Makefile

```bash
# Start production services
make start

# Start development services
make dev

# Stop services
make stop

# View logs
make logs

# Check status
make status

# Clean everything
make clean

# Build images
make build

# Rebuild images (no cache)
make rebuild
```

### Using Scripts

```bash
# Deploy to production
./scripts/deploy-prod.sh

# Create database backup
./scripts/backup-db.sh

# Monitor system
./scripts/monitor.sh

# Start development
./dev.sh

# Start production
./prod.sh
```

## 🔧 Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 80, 443, 8080, and 3306 are available
2. **Permission issues**: Run scripts with proper permissions
3. **Database connection**: Wait for MySQL to fully start before backend
4. **SSL certificate issues**: Check domain DNS and certificate validity

### Reset Everything

```bash
# Stop and remove everything
docker-compose -f docker-compose.prod.yml down -v

# Start fresh
./scripts/deploy-prod.sh
```

### View Service Status

```bash
# Check all services
docker-compose -f docker-compose.prod.yml ps

# Check specific service logs
docker-compose -f docker-compose.prod.yml logs backend
docker-compose -f docker-compose.prod.yml logs frontend
docker-compose -f docker-compose.prod.yml logs mysql
```

## 📋 Deployment Checklist

- [ ] Server prepared with Docker and Docker Compose
- [ ] Domain name configured and pointing to server
- [ ] Environment variables configured in `.env.production`
- [ ] SSL certificate installed
- [ ] Nginx reverse proxy configured
- [ ] Firewall rules set up
- [ ] Database backup strategy in place
- [ ] Monitoring scripts created
- [ ] Application deployed and tested
- [ ] Health checks passing
- [ ] Automated backups configured

## 🆘 Support

For issues and questions:

1. Check the troubleshooting section
2. Review the logs: `docker-compose -f docker-compose.prod.yml logs -f`
3. Run system monitoring: `./scripts/monitor.sh`
4. Create an issue in the repository

## 📄 Access Information

After successful deployment:

- **Frontend**: `https://yourdomain.com`
- **Backend API**: `https://yourdomain.com/api`
- **Health Check**: `https://yourdomain.com/api/actuator/health`
- **Database**: Internal only (not exposed to host)

**Default Login Credentials:**
- Username: `admin`
- Password: `password`

**⚠️ Important**: Change default credentials after first login!
