# Unified Multi-Tenant School Management System

## Overview
This document describes the complete unified multi-tenant school management system that integrates the admin panel with the school management system, allowing centralized management of multiple schools with individual databases and instances.

## 🏗️ System Architecture

### **Multi-Tenant Architecture**
- **Admin Panel**: Central management interface for all schools
- **School Management System**: Individual school instances
- **Database Isolation**: Each school gets its own database
- **Unified Access**: Single Docker Compose file manages everything

### **Components**
1. **Admin Panel** (Port 8082)
   - Multi-tenant management interface
   - School creation and management
   - Real-time dashboard with school statistics
   - User authentication and authorization

2. **School Management System** (Port 80)
   - Individual school instances
   - Student, teacher, and class management
   - Attendance tracking
   - Fee management
   - Report generation

3. **Shared Database** (Port 3306)
   - Admin database: `school_management_system_admin`
   - School databases: `school_management_system_school_XXX`
   - Centralized data management

## 🚀 Quick Start

### **Single Command Startup**
```bash
# Start the entire system
./start-unified-system.sh

# Or with clean volumes
./start-unified-system.sh --clean
```

### **Manual Startup**
```bash
# Build and start all services
docker-compose up --build -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

## 🌐 Access Points

| Service | URL | Description |
|---------|-----|-------------|
| Admin Panel | http://localhost:8082 | Multi-tenant management interface |
| School System | http://localhost:80 | Individual school management |
| Admin API | http://localhost:8081/api | Admin panel backend API |
| School API | http://localhost:8080 | School system backend API |
| Database | localhost:3306 | MySQL database |
| Nginx Proxy | http://localhost:8000 | Optional unified access |

## 🔐 Authentication

### **Admin Panel Login**
- **URL**: http://localhost:8082
- **Username**: `admin`
- **Password**: `admin123`
- **Database**: Stored in `school_management_system_admin` database

### **School System Login**
- Each school gets its own admin user
- **Username**: Set during school creation
- **Password**: `admin123` (default)
- **Database**: Individual school database

## 🎯 Key Features

### **1. Multi-Tenant Admin Panel**
- ✅ **School Creation**: Create new schools with individual databases
- ✅ **Real Dashboard**: Live statistics from actual school data
- ✅ **School Management**: Activate, suspend, delete schools
- ✅ **User Management**: Manage admin users across all schools
- ✅ **Database Integration**: Real-time data from school databases

### **2. School Management System**
- ✅ **Student Management**: Add, edit, promote students
- ✅ **Teacher Management**: Assign teachers to classes
- ✅ **Class Management**: Create and manage class groups
- ✅ **Attendance Tracking**: Daily attendance recording
- ✅ **Fee Management**: Track payments and generate reports
- ✅ **Report Generation**: Academic and financial reports

### **3. Database Integration**
- ✅ **Automatic Database Creation**: Each school gets its own database
- ✅ **Schema Initialization**: School databases are automatically initialized
- ✅ **Real Statistics**: Dashboard shows actual data from school databases
- ✅ **Data Isolation**: Complete separation between schools

## 📊 Dashboard Features

### **Admin Dashboard**
- **Total Schools**: Count of all schools
- **Active Schools**: Schools currently active
- **School Statistics**: Real data from each school
- **System Health**: Service status and monitoring

### **School Dashboard**
- **Student Count**: Total enrolled students
- **Teacher Count**: Total teaching staff
- **Class Count**: Total class groups
- **Recent Activity**: Latest system activities

## 🔧 Technical Implementation

### **Database Schema**

#### **Admin Database** (`school_management_system_admin`)
```sql
-- Schools table
CREATE TABLE schools (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    subdomain VARCHAR(100) UNIQUE NOT NULL,
    database_name VARCHAR(100) NOT NULL,
    plan_type ENUM('basic', 'premium', 'enterprise'),
    status ENUM('active', 'suspended', 'pending'),
    admin_email VARCHAR(255) NOT NULL,
    admin_username VARCHAR(100) NOT NULL
);

-- Admin users table
CREATE TABLE admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('super_admin', 'admin', 'support')
);
```

#### **School Database** (`school_management_system_school_XXX`)
- Complete school management system schema
- Students, teachers, classes, attendance, fees
- Individual school configuration
- School-specific admin user

### **API Endpoints**

#### **Admin Panel API** (`/api`)
- `POST /auth/login` - Admin authentication
- `GET /schools` - List all schools
- `POST /schools` - Create new school
- `GET /schools/{id}/stats` - School statistics
- `GET /dashboard/stats` - Dashboard statistics

#### **School System API** (`/`)
- `POST /auth/login` - School authentication
- `GET /students` - List students
- `POST /students` - Add student
- `GET /teachers` - List teachers
- `GET /classes` - List classes

## 🛠️ Development Workflow

### **1. Create a New School**
1. Login to admin panel (http://localhost:8082)
2. Navigate to "Schools" section
3. Click "Create New School"
4. Fill in school details:
   - School name
   - Subdomain
   - Admin email
   - Plan type
5. Submit - system automatically:
   - Creates school database
   - Initializes schema
   - Creates admin user
   - Sets up school configuration

### **2. Access School System**
1. School gets its own subdomain
2. Login with school admin credentials
3. Manage students, teachers, classes
4. Track attendance and fees
5. Generate reports

### **3. Monitor from Admin Panel**
1. View real-time statistics
2. Monitor school activity
3. Manage school status
4. Access school data

## 📁 File Structure

```
school/
├── docker-compose.yml                    # Unified Docker configuration
├── start-unified-system.sh              # Single command startup
├── multi-tenant/
│   ├── admin-panel/
│   │   ├── backend/                      # Admin panel backend
│   │   │   ├── src/main/java/
│   │   │   │   └── com/devtech/admin/
│   │   │   │       ├── controller/       # API controllers
│   │   │   │       ├── service/          # Business logic
│   │   │   │       ├── entity/           # Database entities
│   │   │   │       └── repository/       # Data access
│   │   │   └── src/main/resources/
│   │   │       └── mysql/init/           # SQL scripts
│   │   └── frontend/                     # Admin panel frontend
│   │       ├── src/
│   │       │   ├── pages/                # React pages
│   │       │   ├── components/           # React components
│   │       │   └── hooks/                # Custom hooks
│   │       └── Dockerfile
│   └── shared/
│       └── nginx/                        # Nginx configuration
├── school-management-system/             # School system backend
├── school-management-frontend/           # School system frontend
└── mysql/
    └── init/                             # Database initialization
```

## 🔄 Data Flow

### **School Creation Process**
1. **Admin Panel** → Create school request
2. **Admin Backend** → Validate and save school record
3. **Integration Service** → Create school database
4. **SQL Scripts** → Initialize school schema
5. **Configuration** → Set up school admin user
6. **Response** → Return school details

### **Dashboard Data Flow**
1. **Admin Frontend** → Request dashboard data
2. **Admin Backend** → Query admin database
3. **Integration Service** → Connect to school databases
4. **Real Data** → Aggregate statistics from all schools
5. **Response** → Return live dashboard data

## 🧪 Testing

### **System Test**
```bash
# Test the entire system
./start-unified-system.sh

# Check all services
docker-compose ps

# Test endpoints
curl http://localhost:8082          # Admin frontend
curl http://localhost:8081/api/actuator/health  # Admin backend
curl http://localhost:80            # School frontend
curl http://localhost:8080/actuator/health      # School backend
```

### **Database Test**
```bash
# Connect to admin database
docker exec -it school_mysql mysql -u root -proot school_management_system_admin

# List all schools
SELECT * FROM schools;

# Connect to school database
docker exec -it school_mysql mysql -u root -proot school_management_system_school_001
```

## 🚨 Troubleshooting

### **Common Issues**

#### **1. Services Not Starting**
```bash
# Check Docker status
docker info

# Check service logs
docker-compose logs [service-name]

# Restart services
docker-compose restart
```

#### **2. Database Connection Issues**
```bash
# Check database status
docker exec school_mysql mysqladmin ping -h localhost

# Check database logs
docker-compose logs mysql
```

#### **3. Frontend Not Loading**
```bash
# Check frontend logs
docker-compose logs admin-frontend
docker-compose logs school-frontend

# Rebuild frontend
docker-compose build admin-frontend
docker-compose build school-frontend
```

### **Useful Commands**
```bash
# View all logs
docker-compose logs -f

# Stop all services
docker-compose down

# Clean restart
docker-compose down -v
docker-compose up --build -d

# Check resource usage
docker stats
```

## 🔮 Future Enhancements

### **Planned Features**
- 🔄 **JWT Authentication**: Secure token-based authentication
- 🔄 **Email Integration**: Automated email notifications
- 🔄 **File Upload**: School logo and document management
- 🔄 **Backup System**: Automated database backups
- 🔄 **Monitoring**: Prometheus and Grafana integration
- 🔄 **API Documentation**: Swagger/OpenAPI documentation

### **Scalability Improvements**
- 🔄 **Load Balancing**: Multiple school instances
- 🔄 **Caching**: Redis for performance optimization
- 🔄 **Microservices**: Service decomposition
- 🔄 **Kubernetes**: Container orchestration

## ✅ Implementation Status

- ✅ **Unified Docker Setup**: Single docker-compose.yml
- ✅ **Multi-Tenant Architecture**: Admin panel + school system
- ✅ **Database Integration**: Real-time data from school databases
- ✅ **Authentication System**: Admin and school login
- ✅ **School Creation**: Automatic database and user setup
- ✅ **Dashboard Integration**: Real statistics from schools
- ✅ **Single Command Startup**: Unified startup script
- ✅ **Documentation**: Comprehensive system documentation

## 🎉 Conclusion

The unified multi-tenant school management system is now **fully functional** and ready for production use. The system provides:

1. **Centralized Management**: Admin panel for all schools
2. **Individual School Instances**: Each school gets its own system
3. **Real-Time Data**: Live statistics from actual school databases
4. **Easy Deployment**: Single command startup
5. **Scalable Architecture**: Ready for multiple schools

**Start the system with**: `./start-unified-system.sh`

**Access the admin panel at**: http://localhost:8082 (admin/admin123)

**Create your first school and experience the full multi-tenant system!**
