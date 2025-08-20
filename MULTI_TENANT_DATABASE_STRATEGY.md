# Multi-Tenant Database Strategy

## Overview

The school management system uses a **database-per-tenant** strategy where each school gets its own MySQL database. This provides complete data isolation and allows for school-specific customizations.

## Database Architecture

### 1. Admin Panel Database
- **Database**: `admin_panel_db` 
- **Purpose**: Stores system administrators, school metadata, subscriptions
- **Tables**: `admin_users`, `schools`, `subscriptions`
- **Access**: Admin panel backend (port 8081)

### 2. Tenant Databases
- **Pattern**: `school_{tenant-id}`
- **Examples**: `school_test-school`, `school_greenwood`, `school_riverside`
- **Purpose**: Complete school management system for each tenant
- **Tables**: All school entities (users, students, teachers, classes, etc.)
- **Access**: School backend (port 8080) with tenant routing

## How Database Connection Works

### 1. Tenant Resolution
```java
// From subdomain: greenwood.localhost -> tenant = "greenwood"
// Database name: school_greenwood
```

### 2. Dynamic DataSource Routing
```java
public class TenantDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getCurrentTenant();
        // Returns tenant ID to route to correct database
        return tenant;
    }
}
```

### 3. Connection Pool Management
- Each tenant gets its own HikariCP connection pool
- Pools are created on-demand when first accessed
- Cached for subsequent requests

## System Administrator Flow

### 1. Admin Panel Users (Port 5173/8081)
These are **system administrators** who:
- Create and manage schools
- Monitor system health
- Handle billing and subscriptions
- **DO NOT** access individual school data

### 2. School Admin Users (Port 3000/8080)
These are **school administrators** who:
- Complete school setup after creation
- Manage their school's users (teachers, students)
- Access only their tenant database
- Created during school setup process

## Database Creation Process

### 1. Admin Creates School
```bash
# Admin panel creates school metadata
POST /api/admin/schools
{
  "name": "Greenwood High",
  "subdomain": "greenwood",
  "adminEmail": "admin@greenwood.edu"
}
```

### 2. Tenant Database Creation
```java
// TenantManagementService automatically:
// 1. Creates database: school_greenwood
// 2. Runs initialization scripts
// 3. Marks school as requiring setup
// 4. Adds to connection pool
```

### 3. School Setup Flow
```bash
# School admin visits: greenwood.localhost:3000
# 1. Redirected to setup page (412 status)
# 2. Completes school configuration
# 3. Creates admin user in tenant database
# 4. School marked as configured
```

## Key Configuration Files

### 1. Application Properties
```properties
# Default database (fallback)
spring.datasource.url=jdbc:mysql://localhost:3306/school_management_system
spring.datasource.username=root
spring.datasource.password=root
```

### 2. Tenant Context (Thread-Local)
```java
// Set per request based on subdomain
TenantContext.setCurrentTenant("greenwood");
// Results in database: school_greenwood
```

### 3. Database Initialization Scripts
```
/resources/mysql/init/
├── 01-init.sql          # Basic schema
├── 02-add-receipt-number.sql
├── 03-create-tables.sql # All entities
├── 04-seed-data.sql     # Default data
└── 06-add-setup-fields.sql # Setup tracking
```

## Security & Isolation

### 1. Complete Data Isolation
- Each school has separate database
- No cross-tenant data access possible
- Schema changes can be school-specific

### 2. Connection Security
- Each tenant pool uses same MySQL credentials
- Database-level isolation via separate schemas
- Connection pooling prevents resource exhaustion

### 3. Admin vs School Access
- **Admin Panel**: Only accesses admin database
- **School System**: Only accesses tenant database
- No shared data between systems

## Deployment Considerations

### 1. Database Scaling
- Each school database can be moved to separate servers
- Connection strings updated in TenantDataSource
- Horizontal scaling by tenant

### 2. Backup Strategy
- Per-tenant backups possible
- School-specific restore capabilities
- Data retention policies per school

### 3. Monitoring
- Connection pool metrics per tenant
- Database performance per school
- Resource usage tracking

## Current System Status

✅ **Admin Panel**: Creates schools and system users
✅ **Tenant Routing**: Dynamic database switching
✅ **School Setup**: User-driven configuration
✅ **Data Isolation**: Complete tenant separation
✅ **Connection Pooling**: Efficient resource usage

The system is fully operational with proper multi-tenant database architecture!