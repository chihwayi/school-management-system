# Multi-Tenant System Testing Guide

## 🎯 Overview

This guide will help you test the complete multi-tenant school management system to ensure everything is working correctly before going live.

## 🚀 Quick Start Testing

### 1. Start the Multi-Tenant System

```bash
# Navigate to the multi-tenant directory
cd multi-tenant

# Start all services
./start-multi-tenant.sh
```

### 2. Run the Automated Test Suite

```bash
# Run comprehensive tests
./test-multi-tenant.sh
```

### 3. Manual Testing Checklist

## 📋 Pre-Testing Checklist

- [ ] Docker Desktop is running
- [ ] Ports 80, 8081, 8082, 3306 are available
- [ ] At least 4GB RAM available
- [ ] Stable internet connection for Docker image downloads

## 🧪 Automated Tests

The `test-multi-tenant.sh` script performs the following tests:

### Database Tests
- ✅ MySQL connection
- ✅ Admin database existence
- ✅ Schools table structure
- ✅ Sample data verification

### Service Tests
- ✅ Admin backend API (port 8081)
- ✅ Admin frontend (port 8082)
- ✅ Nginx proxy (port 80)
- ✅ Docker container health

### Provisioning Tests
- ✅ School creation script syntax
- ✅ Script permissions and availability

## 🎨 Manual Testing Steps

### 1. Admin Panel Access

**URL**: http://localhost:8082

**Expected Results**:
- [ ] Admin panel loads without errors
- [ ] Dashboard displays statistics
- [ ] Navigation works (Dashboard, Schools, etc.)
- [ ] Responsive design on different screen sizes

**Test Cases**:
```bash
# Test admin panel accessibility
curl -I http://localhost:8082
# Should return HTTP 200 OK
```

### 2. Admin API Testing

**Base URL**: http://localhost:8081/api

**Test Endpoints**:
```bash
# Health check
curl http://localhost:8081/api/health

# Dashboard stats
curl http://localhost:8081/api/dashboard/stats

# Schools list
curl http://localhost:8081/api/schools
```

**Expected Results**:
- [ ] All endpoints return valid JSON
- [ ] No CORS errors in browser console
- [ ] Proper HTTP status codes

### 3. Database Verification

**Connect to MySQL**:
```bash
mysql -h localhost -P 3306 -u root -p
# Password: root
```

**Check Admin Database**:
```sql
USE school_management_system_admin;
SHOW TABLES;
SELECT COUNT(*) FROM schools;
SELECT COUNT(*) FROM subscriptions;
```

**Expected Results**:
- [ ] Admin database exists
- [ ] All tables are created
- [ ] Sample data is present

### 4. School Provisioning Test

**Test School Creation**:
```bash
# Navigate to admin panel
# Go to Schools page
# Click "Add School"
# Fill in test data:
#   - School Name: "Test School"
#   - Subdomain: "testschool"
#   - Admin Email: "admin@testschool.com"
#   - Plan: "Basic"
```

**Expected Results**:
- [ ] School creation form works
- [ ] Validation prevents duplicate subdomains
- [ ] School appears in the list after creation

### 5. Nginx Proxy Testing

**Test Proxy Health**:
```bash
curl http://localhost:80/health
# Should return "healthy"
```

**Test Admin Routing**:
```bash
curl -H "Host: admin.yoursystem.com" http://localhost:80
# Should route to admin panel
```

## 🔧 Troubleshooting Common Issues

### Issue: Services Not Starting

**Symptoms**: Docker containers fail to start or exit immediately

**Solutions**:
```bash
# Check Docker logs
docker-compose logs

# Check port availability
netstat -tulpn | grep :80
netstat -tulpn | grep :8081
netstat -tulpn | grep :8082

# Restart Docker Desktop
# Then try again
```

### Issue: Database Connection Failed

**Symptoms**: Admin panel shows database errors

**Solutions**:
```bash
# Check MySQL container
docker-compose logs mysql

# Verify database initialization
docker exec -it multi_tenant_mysql mysql -u root -p -e "SHOW DATABASES;"

# Re-run database initialization
docker-compose down -v
docker-compose up -d mysql
# Wait for MySQL to start, then start other services
```

### Issue: Admin Panel Not Loading

**Symptoms**: Browser shows connection refused or timeout

**Solutions**:
```bash
# Check admin frontend container
docker-compose logs admin-frontend

# Check admin backend container
docker-compose logs admin-backend

# Verify ports are exposed
docker-compose ps
```

### Issue: CORS Errors

**Symptoms**: Browser console shows CORS policy errors

**Solutions**:
```bash
# Check CORS configuration in application.properties
# Verify allowed origins include http://localhost:8082

# Restart admin backend
docker-compose restart admin-backend
```

## 📊 Performance Testing

### Load Testing (Optional)

**Test with Multiple Schools**:
```bash
# Create 5-10 test schools via admin panel
# Monitor system performance
# Check database response times
# Verify no memory leaks
```

**Expected Results**:
- [ ] System remains responsive
- [ ] Database queries complete within 2 seconds
- [ ] No memory usage spikes
- [ ] All schools remain isolated

## 🔒 Security Testing

### Basic Security Checks

**Test Cases**:
- [ ] Admin panel requires authentication (future feature)
- [ ] API endpoints validate input
- [ ] No sensitive data in browser console
- [ ] HTTPS redirects work (when SSL is configured)

## 📈 Monitoring and Logs

### View Real-time Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f admin-backend
docker-compose logs -f admin-frontend
docker-compose logs -f mysql
```

### Health Monitoring

```bash
# Check service health
docker-compose ps

# Monitor resource usage
docker stats
```

## ✅ Success Criteria

The multi-tenant system is ready for production when:

### Functional Requirements
- [ ] Admin panel loads and functions correctly
- [ ] All API endpoints respond properly
- [ ] Database operations work without errors
- [ ] School provisioning process is functional
- [ ] Nginx proxy routes requests correctly

### Performance Requirements
- [ ] Page load times < 3 seconds
- [ ] API response times < 2 seconds
- [ ] Database queries < 1 second
- [ ] System handles multiple concurrent users

### Reliability Requirements
- [ ] Services restart automatically after failures
- [ ] Database connections are stable
- [ ] No memory leaks during extended use
- [ ] Error handling works correctly

## 🚀 Next Steps After Testing

1. **Configure Production Settings**:
   - Update domain names
   - Set secure passwords
   - Configure SSL certificates
   - Set up monitoring

2. **Create First Real School**:
   - Use admin panel to create production school
   - Test school-specific functionality
   - Verify data isolation

3. **Backup Strategy**:
   - Set up automated database backups
   - Test backup and restore procedures
   - Document recovery processes

4. **Monitoring Setup**:
   - Configure health checks
   - Set up alerting
   - Monitor system metrics

## 📞 Support

If you encounter issues during testing:

1. Check the troubleshooting section above
2. Review Docker logs for error messages
3. Verify all prerequisites are met
4. Consult the main README.md for detailed setup instructions

---

**Happy Testing! 🎉**

The multi-tenant system is designed to be robust and scalable. With proper testing, you'll have confidence in deploying it to production.


