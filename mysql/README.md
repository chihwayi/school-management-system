# Database Migration and Deployment Guide

## Overview

This directory contains all database-related files for the School Management System, including migrations, seed data, and deployment scripts.

## File Structure

```
mysql/
├── README.md                    # This file
├── init/
│   ├── 01-init.sql             # Database and user creation
│   ├── 02-add-receipt-number.sql # Receipt number migration
│   ├── 03-create-tables.sql    # Complete schema creation
│   └── 04-seed-data.sql        # Initial data population
└── scripts/
    ├── backup.sh               # Database backup script
    ├── restore.sh              # Database restore script
    └── migrate.sh              # Migration runner script
```

## Migration Files

### 1. `01-init.sql` - Database Initialization
- Creates the database if it doesn't exist
- Sets up database user with proper permissions
- Configures timezone settings
- **Safe to run multiple times** (uses `IF NOT EXISTS`)

### 2. `02-add-receipt-number.sql` - Receipt Number Feature
- Adds `receipt_number` column to `fee_payments` table
- Creates unique index for performance
- **Safe to run multiple times** (uses `IF NOT EXISTS`)

### 3. `03-create-tables.sql` - Complete Schema
- Creates all database tables with proper relationships
- Includes all indexes for optimal performance
- **Safe to run multiple times** (uses `IF NOT EXISTS`)

### 4. `04-seed-data.sql` - Initial Data
- Populates tables with sample data
- Creates default users and roles
- Sets up sample students, teachers, and subjects
- **Safe to run multiple times** (uses `INSERT IGNORE`)

## Deployment Process

### Development Environment

1. **Start MySQL container:**
   ```bash
   docker-compose up mysql -d
   ```

2. **Run migrations manually:**
   ```bash
   # Connect to MySQL container
   docker exec -it school_mysql mysql -u root -p
   
   # Run migrations in order
   source /docker-entrypoint-initdb.d/01-init.sql
   source /docker-entrypoint-initdb.d/02-add-receipt-number.sql
   source /docker-entrypoint-initdb.d/03-create-tables.sql
   source /docker-entrypoint-initdb.d/04-seed-data.sql
   ```

### Production Environment

1. **Backup existing database (if any):**
   ```bash
   ./mysql/scripts/backup.sh
   ```

2. **Run migrations:**
   ```bash
   ./mysql/scripts/migrate.sh
   ```

3. **Verify deployment:**
   ```bash
   # Check table counts
   mysql -u root -p school_management_system -e "
   SELECT 'Roles' as table_name, COUNT(*) as count FROM roles
   UNION ALL
   SELECT 'Users' as table_name, COUNT(*) as count FROM users
   UNION ALL
   SELECT 'Students' as table_name, COUNT(*) as count FROM students;
   "
   ```

## Database Schema

### Core Tables

| Table | Purpose | Key Features |
|-------|---------|--------------|
| `users` | User accounts | Authentication, roles |
| `roles` | User roles | ADMIN, CLERK, TEACHER, CLASS_TEACHER |
| `students` | Student records | Academic info, enrollment |
| `teachers` | Teacher records | Employment, signatures |
| `subjects` | Academic subjects | Categories, codes |
| `sections` | Class sections | Green, Blue, Red, Yellow |
| `class_groups` | Class organization | Form + Section combinations |
| `guardians` | Parent/guardian info | Contact details, WhatsApp |

### Academic Tables

| Table | Purpose | Key Features |
|-------|---------|--------------|
| `attendance` | Daily attendance | Present/absent tracking |
| `assessments` | Academic assessments | Coursework, exams, grades |
| `reports` | Student reports | Term reports, comments |
| `subject_reports` | Subject-specific reports | Individual subject grades |

### Financial Tables

| Table | Purpose | Key Features |
|-------|---------|--------------|
| `fee_settings` | Fee configuration | Amounts by level/term |
| `fee_payments` | Payment records | **Receipt numbers**, balances |

### Relationship Tables

| Table | Purpose | Key Features |
|-------|---------|--------------|
| `user_roles` | User-role assignments | Many-to-many relationship |
| `teacher_subject_class` | Teacher assignments | Subject + class assignments |
| `student_subjects` | Student enrollments | Subject enrollment tracking |

## Default Users

| Username | Password | Role | Purpose |
|----------|----------|------|---------|
| `admin` | `admin123` | ADMIN + TEACHER | System administration |
| `clerk` | `clerk123` | CLERK | Fee management, reports |
| `teacher` | `teacher123` | TEACHER | Subject teaching |
| `classteacher` | `classteacher123` | TEACHER + CLASS_TEACHER | Class management |

## Receipt Number System

### Format
- **Pattern**: `RCPT-YYYY-XXXXX`
- **Example**: `RCPT-2025-00001`, `RCPT-2025-00002`
- **Logic**: Year-based sequential numbering

### Features
- ✅ **Unique**: Each receipt number is unique
- ✅ **Sequential**: Numbers increment automatically
- ✅ **Year-based**: Resets each academic year
- ✅ **Database-stored**: Persisted in database
- ✅ **Auto-generated**: Created automatically on payment

### Database Column
```sql
ALTER TABLE fee_payments ADD COLUMN receipt_number VARCHAR(20) UNIQUE;
CREATE INDEX idx_fee_payments_receipt_number ON fee_payments(receipt_number);
```

## Performance Indexes

The database includes comprehensive indexing for optimal performance:

### Student Queries
- `idx_students_form_section` - Class-based queries
- `idx_students_academic_year` - Year-based queries
- `idx_students_student_id` - Student ID lookups

### Attendance Queries
- `idx_attendance_date` - Date-based queries
- `idx_attendance_student_date` - Student attendance

### Payment Queries
- `idx_fee_payments_student_term_year` - Student payments
- `idx_fee_payments_date` - Date-based queries
- `idx_fee_payments_status` - Payment status queries
- `idx_fee_payments_receipt_number` - Receipt lookups

### Report Queries
- `idx_reports_student_term_year` - Student reports
- `idx_reports_finalized` - Finalized reports

## Backup and Recovery

### Backup Script
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p school_management_system > backup_${DATE}.sql
```

### Restore Script
```bash
#!/bin/bash
mysql -u root -p school_management_system < backup_file.sql
```

## Troubleshooting

### Common Issues

1. **Migration fails with "table already exists"**
   - All migrations use `IF NOT EXISTS` - safe to re-run
   - Check for syntax errors in SQL files

2. **Seed data not inserted**
   - Uses `INSERT IGNORE` - safe to re-run
   - Check foreign key constraints

3. **Performance issues**
   - Verify indexes are created
   - Check query execution plans

4. **Receipt number conflicts**
   - Ensure `receipt_number` column is unique
   - Check for duplicate generation logic

### Verification Queries

```sql
-- Check table structure
DESCRIBE fee_payments;

-- Check receipt number column
SELECT receipt_number FROM fee_payments LIMIT 5;

-- Check indexes
SHOW INDEX FROM fee_payments;

-- Check data counts
SELECT COUNT(*) FROM students;
SELECT COUNT(*) FROM teachers;
SELECT COUNT(*) FROM fee_payments;
```

## Security Considerations

### Production Deployment

1. **Change default passwords**
   ```sql
   UPDATE users SET password = 'new_hashed_password' WHERE username = 'admin';
   ```

2. **Use environment variables**
   ```bash
   export MYSQL_ROOT_PASSWORD=secure_password
   export MYSQL_USER=app_user
   export MYSQL_PASSWORD=app_password
   ```

3. **Restrict database access**
   ```sql
   GRANT SELECT, INSERT, UPDATE, DELETE ON school_management_system.* TO 'app_user'@'%';
   REVOKE ALL PRIVILEGES ON school_management_system.* FROM 'root'@'%';
   ```

4. **Enable SSL connections**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/school_management_system?useSSL=true&requireSSL=true
   ```

## Version Control

### Migration Versioning
- Each migration file is numbered sequentially
- Include version and date in file headers
- Document changes in this README

### Rollback Strategy
- Keep backup before each migration
- Test migrations in staging environment
- Document rollback procedures

## Support

For database-related issues:
1. Check this README for common solutions
2. Review migration logs
3. Verify database connectivity
4. Check application logs for errors
