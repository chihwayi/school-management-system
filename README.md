# School Management System

A comprehensive school management system with React frontend and Spring Boot backend, fully containerized with Docker.

## 🏗️ Architecture

- **Frontend**: React + TypeScript + Vite
- **Backend**: Spring Boot + Java 21
- **Database**: MySQL 8.0
- **Containerization**: Docker + Docker Compose

## 🚀 Quick Start

### Prerequisites

- Docker Desktop installed and running
- Git

### Running the Application

1. **Clone the repository** (if not already done):
   ```bash
   git clone <your-repo-url>
   cd school
   ```

2. **Start all services**:
   ```bash
   npm run dev
   ```
   or
   ```bash
   ./dev.sh
   ```

3. **Access the application**:
   - School Frontend: http://localhost:3000
   - Admin Panel: http://localhost:5173
   - School Backend API: http://localhost:8080/api
   - Admin Backend API: http://localhost:8081/api
   - Database: localhost:3306

### Available Commands

```bash
# Start the complete system
npm run dev

# Check system status
npm run status

# View logs
npm run logs

# Stop all services
npm run down

# Restart services
npm run restart

# Clean up everything
npm run clean
```

## 📁 Project Structure

```
school/
├── docker-compose.yml          # Main orchestration file
├── dev.sh                     # Bulletproof startup script
├── package.json               # NPM scripts for easy management
├── school-management-frontend/ # React frontend
│   ├── Dockerfile             # Frontend container
│   ├── nginx.conf             # Nginx configuration
│   └── .dockerignore          # Docker ignore file
├── school-management-system/   # Spring Boot backend
│   ├── Dockerfile             # Backend container
│   ├── .dockerignore          # Docker ignore file
│   └── src/main/resources/
│       └── application-docker.properties # Docker config
└── mysql/                     # Database initialization
    └── init/
        └── 01-init.sql        # Database setup script
```

## 🔧 Configuration

### Environment Variables

The application uses the following environment variables:

- `MYSQL_ROOT_PASSWORD`: MySQL root password (default: root)
- `MYSQL_DATABASE`: Database name (default: school_management_system)
- `SPRING_PROFILES_ACTIVE`: Spring profile (default: docker)

### Ports

- **3000**: School Frontend (React)
- **5173**: Admin Panel Frontend (React)
- **8080**: School Backend API (Spring Boot)
- **8081**: Admin Backend API (Spring Boot)
- **3306**: MySQL Database
- **8000**: Nginx Proxy

### Volumes

- `mysql_data`: Persistent MySQL data
- `backend_uploads`: File uploads for the backend

## 🛠️ Development

### Frontend Development

```bash
cd school-management-frontend
npm install
npm run dev
```

### Backend Development

```bash
cd school-management-system
./mvnw spring-boot:run
```

### Database Access

```bash
# Connect to MySQL container
docker exec -it school_mysql mysql -u root -p

# Or use a MySQL client
mysql -h localhost -P 3306 -u root -p
```

## 📊 Monitoring

### Health Checks

All services include health checks:

- **Frontend**: HTTP check on port 80
- **Backend**: Spring Boot Actuator health endpoint
- **Database**: MySQL ping command

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f mysql
```

## 🔒 Security

### Production Considerations

1. **Change default passwords** in `docker-compose.yml`
2. **Use environment files** for sensitive data
3. **Enable HTTPS** with proper SSL certificates
4. **Configure proper JWT secrets**
5. **Set up proper database user permissions**

### Environment File Setup

Create a `.env` file in the root directory:

```env
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_USER=your_db_user
MYSQL_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
```

## 🐛 Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 80, 8080, and 3306 are available
2. **Permission issues**: Run scripts with proper permissions
3. **Database connection**: Wait for MySQL to fully start before backend

### Reset Everything

```bash
# Stop and remove everything
./scripts/clean.sh

# Start fresh
./scripts/start.sh
```

### View Service Status

```bash
docker-compose ps
```

## 📝 API Documentation

Once the backend is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test with Docker
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions:

1. Check the troubleshooting section
2. Review the logs: `docker-compose logs -f`
3. Create an issue in the repository
