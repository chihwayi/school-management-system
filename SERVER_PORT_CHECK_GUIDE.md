# School Management System - Server Port Check Guide

This guide will help you check if all required ports are available and properly configured on your server before deploying the school management system.

## Ports Used by Your School Management System

Based on your configuration files, here are the ports your system uses:

### Development Environment
- **Port 3000**: React Frontend (Development)
- **Port 8080**: Spring Boot Backend
- **Port 3306**: MySQL Database
- **Port 5005**: Java Debug Port (Development only)

### Production Environment
- **Port 80**: HTTP (Nginx Reverse Proxy)
- **Port 443**: HTTPS (Nginx Reverse Proxy)
- **Port 8080**: Spring Boot Backend (Internal only)
- **Port 3306**: MySQL Database (Internal only)

## Step 1: Upload the Port Checker Script

1. **Copy the script to your server:**
   ```bash
   # Upload the check-ports.sh script to your server
   scp scripts/check-ports.sh user@your-server:/home/user/
   ```

2. **Make it executable:**
   ```bash
   ssh user@your-server
   chmod +x check-ports.sh
   ```

## Step 2: Run the Port Checker

Execute the script on your server:

```bash
./check-ports.sh
```

This will check:
- ✅ Port availability
- ✅ Port listening status
- ✅ Port connectivity
- ✅ Docker status
- ✅ Firewall configuration
- ✅ System information

## Step 3: Manual Port Checks (Alternative)

If you prefer to check ports manually, use these commands:

### Check if ports are in use:
```bash
# Check specific ports
sudo lsof -i :3000
sudo lsof -i :8080
sudo lsof -i :3306
sudo lsof -i :80
sudo lsof -i :443
sudo lsof -i :5005

# Check all listening ports
sudo netstat -tuln
# or
sudo ss -tuln
```

### Test port connectivity:
```bash
# Test if ports are reachable
telnet localhost 3000
telnet localhost 8080
telnet localhost 3306
telnet localhost 80
telnet localhost 443

# Or use nc (netcat)
nc -zv localhost 3000
nc -zv localhost 8080
nc -zv localhost 3306
nc -zv localhost 80
nc -zv localhost 443
```

### Check what's using a specific port:
```bash
# Find process using port
sudo lsof -i :PORT_NUMBER

# Kill process if needed
sudo kill -9 PID_NUMBER
```

## Step 4: Check Firewall Configuration

### Ubuntu/Debian (UFW):
```bash
# Check UFW status
sudo ufw status

# Allow required ports
sudo ufw allow 3000/tcp  # Development frontend
sudo ufw allow 8080/tcp  # Backend
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS

# For production, you might want to restrict 8080 and 3306 to localhost only
sudo ufw allow from 127.0.0.1 to any port 8080
sudo ufw allow from 127.0.0.1 to any port 3306
```

### CentOS/RHEL (Firewalld):
```bash
# Check firewalld status
sudo systemctl status firewalld
sudo firewall-cmd --list-all

# Allow required ports
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --reload
```

### Check iptables:
```bash
# List iptables rules
sudo iptables -L -n

# Check if ports are blocked
sudo iptables -L INPUT -n | grep -E "(DROP|REJECT)"
```

## Step 5: Check Docker Status

```bash
# Check if Docker is running
sudo systemctl status docker

# Start Docker if not running
sudo systemctl start docker
sudo systemctl enable docker

# Check Docker containers using ports
docker ps --format "table {{.Names}}\t{{.Ports}}"

# Check if ports are available for Docker
docker run --rm -p 3000:3000 hello-world
```

## Step 6: Production Security Checklist

For production deployment, ensure:

### ✅ Port Security:
- [ ] Only ports 80 and 443 are exposed to the internet
- [ ] Ports 8080 and 3306 are internal only (not accessible from outside)
- [ ] Use reverse proxy (Nginx) for production
- [ ] SSL/TLS certificate is configured for port 443

### ✅ Firewall Rules:
- [ ] UFW/Firewalld is active
- [ ] Only necessary ports are open
- [ ] Database port (3306) is restricted to localhost
- [ ] Backend port (8080) is restricted to localhost

### ✅ Docker Security:
- [ ] Containers run with non-root users
- [ ] Network isolation is properly configured
- [ ] Secrets are not hardcoded in images

## Step 7: Troubleshooting Common Issues

### Port Already in Use:
```bash
# Find what's using the port
sudo lsof -i :PORT_NUMBER

# Stop the service
sudo systemctl stop SERVICE_NAME

# Or kill the process
sudo kill -9 PID_NUMBER
```

### Port Not Accessible:
```bash
# Check if service is listening
sudo netstat -tuln | grep :PORT_NUMBER

# Check firewall rules
sudo ufw status
sudo firewall-cmd --list-all

# Check if Docker is binding correctly
docker ps
```

### Database Connection Issues:
```bash
# Check MySQL status
sudo systemctl status mysql

# Check MySQL port
sudo netstat -tuln | grep :3306

# Test MySQL connection
mysql -h localhost -P 3306 -u root -p
```

## Step 8: Quick Commands Reference

```bash
# Check all ports at once
for port in 3000 8080 3306 80 443 5005; do
  echo "Port $port:"
  sudo lsof -i :$port || echo "  Available"
done

# Test all ports connectivity
for port in 3000 8080 3306 80 443; do
  echo -n "Port $port: "
  nc -zv localhost $port 2>&1 | grep -q "succeeded" && echo "✅ Open" || echo "❌ Closed"
done

# Check Docker port usage
docker ps --format "table {{.Names}}\t{{.Ports}}" | grep -E ":(3000|8080|3306|80|443|5005)"

# Check system resources
free -h
df -h
uptime
```

## Step 9: Deploy After Port Check

Once all ports are available:

### Development:
```bash
docker-compose -f docker-compose.dev.yml up -d
```

### Production:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Expected Results

After running the port checker, you should see:

### ✅ For Development:
- Port 3000: Available or in use by your frontend
- Port 8080: Available or in use by your backend
- Port 3306: Available or in use by MySQL
- Port 5005: Available (debug port)

### ✅ For Production:
- Port 80: Available for Nginx
- Port 443: Available for HTTPS
- Port 8080: Internal only (not exposed)
- Port 3306: Internal only (not exposed)

## Need Help?

If you encounter issues:

1. **Check the script output** for specific error messages
2. **Verify firewall rules** are not blocking ports
3. **Ensure Docker is running** and has proper permissions
4. **Check system resources** (memory, disk space)
5. **Review Docker logs** for container-specific issues

Run the port checker script and share the output if you need further assistance!
