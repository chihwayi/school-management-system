# School Management System

A comprehensive school management system built with Spring Boot, designed to handle student records, assessments, attendance, and reporting for educational institutions.

## Features

- **School Configuration**: One-time setup with branding (colors, logos, backgrounds)
- **User Management**: Admin, Clerk, Teacher, and Class Teacher roles
- **Student Management**: Registration, class assignments, subject enrollment
- **Assessment System**: Coursework and exam recording with weighted grading
- **Attendance Tracking**: Daily attendance with WhatsApp notifications to guardians
- **Report Generation**: Term reports with subject-wise comments
- **Teacher Assignments**: Subject and class assignments for teachers
- **Guardian Management**: Primary guardian contact details with WhatsApp integration

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: MySQL 8.0
- **Security**: JWT Authentication with Spring Security
- **File Upload**: MultipartFile handling for images
- **Messaging**: WhatsApp integration via Twilio API
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup Instructions

### 1. Database Setup

Create a MySQL database:
```sql
CREATE DATABASE school_management_system;
```

### 2. Configuration

Update the database credentials in `application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. WhatsApp Integration (Optional)

To enable WhatsApp notifications, add your Twilio credentials:
```properties
twilio.account.sid=your_account_sid
twilio.auth.token=your_auth_token
twilio.whatsapp.from=whatsapp:+14155238886
```

### 4. File Upload Directory

Create the uploads directory or modify the path in `application.properties`:
```properties
file.upload.directory=./uploads
```

### 5. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/check-school-setup` - Check if school is configured

### School Configuration
- `GET /api/school/config` - Get school configuration
- `POST /api/school/setup` - Initial school setup
- `PUT /api/school/{id}` - Update school configuration

### Student Management
- `GET /api/students/all` - Get all students
- `POST /api/students/create` - Create new student
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### Teacher Management
- `GET /api/teachers/all` - Get all teachers
- `POST /api/teachers` - Create new teacher
- `GET /api/teachers/current` - Get current teacher profile

### Assessment Management
- `POST /api/assessments` - Record assessment
- `GET /api/assessments/student/{studentId}/subject/{subjectId}` - Get student assessments
- `PUT /api/assessments/{id}` - Update assessment

### Attendance
- `POST /api/attendance` - Mark attendance
- `GET /api/attendance/student/{studentId}` - Get student attendance
- `GET /api/attendance/date/{date}` - Get attendance by date

### Reports
- `POST /api/reports/generate/class/{classGroupId}/term/{term}/year/{year}` - Generate class reports
- `GET /api/reports/student/{studentId}` - Get student reports
- `POST /api/reports/{reportId}/subject-comment` - Add subject comment

## Initial Setup Flow

1. **First Time Access**: System redirects to school configuration page
2. **School Setup**: Admin configures school details, colors, and logos
3. **User Creation**: Admin creates clerk and teacher accounts
4. **Student Registration**: Clerk registers students and assigns classes
5. **Subject Assignment**: Clerk assigns subjects to students and teachers
6. **Daily Operations**: Teachers mark attendance and record assessments

## Security Features

- JWT-based authentication
- Role-based access control (RBAC)
- CORS configuration for frontend integration
- Method-level security annotations
- School configuration filter for setup enforcement

## Grade Levels

### O Level (Form 1-4)
- **Languages**: English, Shona, Ndebele
- **Arts**: History, Family and Religious Studies, Heritage Studies, Literature
- **Commercials**: Principles of Accounts, Commerce, Business Enterprise Skills, Economics
- **Sciences**: Mathematics, Combined Science, Biology, Chemistry, Physics, Geography, Agriculture

### A Level (Form 5-6)
- **Arts**: History, Family and Religious Studies, Indigenous Languages, Literature, Heritage, Sociology
- **Commercials**: Accounting, Business Studies, Business Enterprise Skills, Economics
- **Sciences**: Pure Mathematics, Statistics, Geography, Biology, Chemistry, Physics, Agriculture

## Grading System

- **O Level**: A, B, C, D, E, U
- **A Level**: A, B, C, D, E, U or Points system (5-0)

## Support

For technical support or questions, please contact the development team.

## License

This project is proprietary software developed for educational institutions.