# School Management System - Testing Deployment Guide

This guide will help you deploy the school management system for testing by your QA/Testing teams.

## 🎯 Testing Environment Overview

The testing environment is configured with:
- ✅ **Relaxed security** for easier testing
- ✅ **Verbose logging** for debugging
- ✅ **Exposed ports** for testing team access
- ✅ **Testing-specific configurations**
- ✅ **Optional testing tools** (Redis, Mailhog)

## 📁 Files Created for Testing

1. **`env.testing`** - Testing environment variables
2. **`application-testing.properties`** - Spring Boot testing configuration
3. **`docker-compose.testing.yml`** - Testing Docker Compose setup
4. **`nginx/nginx-testing.conf`** - Nginx configuration for testing

## 🚀 Quick Deployment Commands

### 1. Deploy Testing Environment
```bash
# Navigate to your project directory
cd /opt/school-management-system

# Deploy testing environment
docker-compose -f docker-compose.testing.yml up -d
```

### 2. Check Deployment Status
```bash
# Check if all containers are running
docker-compose -f docker-compose.testing.yml ps

# Check logs
docker-compose -f docker-compose.testing.yml logs -f
```

### 3. Access the Application
- **Frontend**: http://your-server-ip:3000
- **Backend API**: http://your-server-ip:8080
- **Database**: your-server-ip:3306
- **Mailhog (Email Testing)**: http://your-server-ip:8025
- **Redis**: your-server-ip:6379

## ⚙️ Configuration Details

### Environment Variables (`env.testing`)
```bash
# Database (Same as development for consistency)
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=school_management_system
MYSQL_USER=school_user
MYSQL_PASSWORD=school_password

# Application
SPRING_PROFILES_ACTIVE=testing
NODE_ENV=testing

# JWT (Same as development)
JWT_SECRET=myVerySecretJWTKeyForSchoolManagementSystem2024DevelopmentOnly

# Testing specific
TESTING_MODE=true
TESTING_DATA_ENABLED=true
TESTING_AI_ENABLED=true
TESTING_EMAIL_ENABLED=false
TESTING_SMS_ENABLED=false
```

### Ports Exposed for Testing
| Service | Port | Purpose |
|---------|------|---------|
| Frontend | 3000 | React application |
| Backend | 8080 | Spring Boot API |
| MySQL | 3306 | Database access |
| Redis | 6379 | Cache/Sessions |
| Mailhog SMTP | 1025 | Email testing |
| Mailhog Web | 8025 | Email testing UI |
| Nginx HTTP | 80 | Reverse proxy |
| Nginx HTTPS | 443 | SSL reverse proxy |

## 🔧 Testing Team Access

### Frontend Testing
```bash
# Access the application
http://your-server-ip:3000

# Test different user roles
# - Admin: admin@school.com / admin123
# - Teacher: teacher@school.com / teacher123
# - Student: student@school.com / student123
# - Parent: parent@school.com / parent123
```

### Backend API Testing
```bash
# Health check
curl http://your-server-ip:8080/actuator/health

# API endpoints
curl http://your-server-ip:8080/api/school/config
curl http://your-server-ip:8080/api/auth/login
```

### Database Testing
```bash
# Connect to MySQL
mysql -h your-server-ip -P 3306 -u school_user -p school_management_system

# Or use MySQL client
mysql://school_user:school_password@your-server-ip:3306/school_management_system
```

## 🧪 Testing Tools Included

### 1. Mailhog (Email Testing)
- **Web UI**: http://your-server-ip:8025
- **SMTP Server**: your-server-ip:1025
- Captures all outgoing emails for testing

### 2. Redis (Caching/Sessions)
- **Port**: 6379
- **Purpose**: Session storage, caching
- **Access**: your-server-ip:6379

### 3. Verbose Logging
- **Backend logs**: `./logs/testing/`
- **Nginx logs**: `./logs/nginx/testing/`
- **Docker logs**: `docker-compose -f docker-compose.testing.yml logs`

## 🔍 Testing Checklist

### ✅ Basic Functionality
- [ ] Application loads on port 3000
- [ ] API responds on port 8080
- [ ] Database connection works
- [ ] User authentication works
- [ ] All user roles can login

### ✅ Feature Testing
- [ ] Student management
- [ ] Teacher management
- [ ] Class management
- [ ] Attendance tracking
- [ ] Grade management
- [ ] Fee management
- [ ] Report generation
- [ ] AI features (if enabled)

### ✅ Integration Testing
- [ ] Email notifications (via Mailhog)
- [ ] File uploads
- [ ] PDF generation
- [ ] Excel exports
- [ ] API endpoints
- [ ] Database operations

### ✅ Performance Testing
- [ ] Load testing
- [ ] Concurrent users
- [ ] Database performance
- [ ] File upload limits
- [ ] Memory usage

## 🛠️ Troubleshooting

### Common Issues

#### 1. Port Conflicts
```bash
# Check what's using the ports
sudo lsof -i :3000
sudo lsof -i :8080
sudo lsof -i :3306

# Stop conflicting services
sudo systemctl stop apache2  # if Apache is running
sudo systemctl stop nginx    # if Nginx is running
```

#### 2. Container Issues
```bash
# Check container status
docker-compose -f docker-compose.testing.yml ps

# Restart specific service
docker-compose -f docker-compose.testing.yml restart backend

# View logs
docker-compose -f docker-compose.testing.yml logs backend
```

#### 3. Database Connection Issues
```bash
# Check MySQL container
docker-compose -f docker-compose.testing.yml logs mysql

# Test database connection
docker exec -it school_mysql_testing mysql -u root -p
```

#### 4. Frontend Issues
```bash
# Check frontend container
docker-compose -f docker-compose.testing.yml logs frontend

# Rebuild frontend
docker-compose -f docker-compose.testing.yml up -d --build frontend
```

## 📊 Monitoring and Logs

### View Logs
```bash
# All services
docker-compose -f docker-compose.testing.yml logs -f

# Specific service
docker-compose -f docker-compose.testing.yml logs -f backend
docker-compose -f docker-compose.testing.yml logs -f frontend
docker-compose -f docker-compose.testing.yml logs -f mysql
```

### Monitor Resources
```bash
# Container resource usage
docker stats

# System resources
htop
df -h
free -h
```

## 🔄 Updates and Maintenance

### Update Application
```bash
# Pull latest changes
git pull origin main

# Rebuild and restart
docker-compose -f docker-compose.testing.yml down
docker-compose -f docker-compose.testing.yml up -d --build
```

### Backup Testing Data
```bash
# Backup database
docker exec school_mysql_testing mysqldump -u root -p school_management_system > testing_backup.sql

# Backup uploads
tar -czf testing_uploads_backup.tar.gz ./uploads/
```

### Clean Up
```bash
# Stop and remove containers
docker-compose -f docker-compose.testing.yml down

# Remove volumes (WARNING: This will delete all data)
docker-compose -f docker-compose.testing.yml down -v

# Clean up Docker system
docker system prune -a
```

## 🚨 Security Notes for Testing

⚠️ **Important**: This testing configuration is NOT secure for production:

- ✅ Passwords are default/weak
- ✅ SSL is optional
- ✅ CORS is permissive
- ✅ Debug logging is enabled
- ✅ All ports are exposed

**Do NOT use this configuration in production!**

## 📞 Support

If you encounter issues:

1. **Check the logs** first
2. **Verify port availability** using the port checker script
3. **Check Docker status** and container health
4. **Review the troubleshooting section** above
5. **Contact the development team** with specific error messages

## 🎉 Ready for Testing!

Your testing environment is now ready for your QA/Testing teams. They can access:

- **Application**: http://your-server-ip:3000
- **API**: http://your-server-ip:8080
- **Email Testing**: http://your-server-ip:8025
- **Database**: your-server-ip:3306

Happy testing! 🧪
