# Attendance Counting Logic and Twilio WhatsApp Implementation

## Overview

This document outlines the implementation of attendance counting logic for reports and Twilio WhatsApp integration for absentee notifications.

## 1. Attendance Counting Logic for Reports

### Features Implemented

#### 1.1 Attendance Statistics Calculation
- **Term-based attendance tracking**: Calculates attendance for specific terms and academic years
- **School days calculation**: Excludes weekends (configurable for holidays)
- **Attendance percentage**: Calculates percentage of days attended vs total school days
- **Present/Absent counting**: Tracks both present and absent days separately

#### 1.2 Key Components

##### AttendanceStatistics DTO
```java
public class AttendanceStatistics {
    private int presentDays;
    private int absentDays;
    private int totalSchoolDays;
    private double attendancePercentage;
}
```

##### AttendanceService Methods
- `calculateTermAttendance(Long studentId, String term, String academicYear)`: Calculates attendance for a specific student and term
- `getClassTermAttendance(String form, String section, String term, String academicYear)`: Calculates attendance for all students in a class
- `calculateTotalSchoolDays(LocalDate startDate, LocalDate endDate)`: Calculates total school days excluding weekends

##### Term Date Configuration
The system uses configurable term dates:
- **Term 1**: January 15 - April 30
- **Term 2**: May 1 - August 31  
- **Term 3**: September 1 - December 31

*Note: These dates can be adjusted in the `AttendanceService` methods `getTermStartDate()` and `getTermEndDate()`*

#### 1.3 Report Integration

##### Report Entity Updates
The `Report` entity includes attendance fields:
- `attendanceDays`: Number of days the student was present
- `totalSchoolDays`: Total number of school days in the term

##### ReportService Methods
- `updateReportAttendance(Long reportId, String term, String academicYear)`: Updates a single report with attendance data
- `updateClassReportsAttendance(String form, String section, String term, String academicYear)`: Updates all reports in a class

##### API Endpoints
- `POST /api/reports/{reportId}/attendance`: Update attendance for a specific report
- `POST /api/reports/class/{form}/{section}/attendance`: Update attendance for all reports in a class

#### 1.4 Frontend Integration

##### ReportService Methods
```typescript
updateReportAttendance(reportId: number, term: string, academicYear: string): Promise<void>
updateClassAttendance(form: string, section: string, term: string, academicYear: string): Promise<void>
```

##### Report Interface Updates
```typescript
export interface Report {
  // ... existing fields
  attendanceDays?: number;
  totalSchoolDays?: number;
  attendancePercentage?: number;
}
```

## 2. Twilio WhatsApp Implementation

### Features Implemented

#### 2.1 Zimbabwe Phone Number Validation
- **Pattern matching**: Validates Zimbabwe mobile numbers using regex
- **Format support**: Supports multiple input formats:
  - `+263 77 123 4567`
  - `077 123 4567`
  - `263 77 123 4567`
- **Auto-formatting**: Converts to international format for WhatsApp API

#### 2.2 WhatsApp Service Features

##### Environment Configuration
```properties
# Twilio WhatsApp Configuration
twilio.enabled=false
twilio.account.sid=your_account_sid_here
twilio.auth.token=your_auth_token_here
twilio.whatsapp.from=whatsapp:+14155238886
```

##### Key Methods
- `sendAbsenteeNotification(Student student, List<Guardian> guardians)`: Sends notifications when students are absent
- `isValidZimbabwePhoneNumber(String phoneNumber)`: Validates phone number format
- `formatPhoneNumberForWhatsApp(String phoneNumber)`: Formats for Twilio API
- `formatPhoneNumberForDisplay(String phoneNumber)`: Formats for UI display

#### 2.3 Integration with Attendance System

##### Automatic Notifications
When a student is marked absent:
1. System finds the primary guardian
2. Validates the WhatsApp number format
3. Sends a formatted message via Twilio
4. Logs success/failure for monitoring

##### Message Template
```
Dear Parent/Guardian,

We would like to inform you that your child [Student Name] (ID: [Student ID]) was marked absent from school today.

If this is an error or if you have any concerns, please contact the school immediately.

Thank you,
School Administration
```

#### 2.4 Frontend Validation

##### Phone Number Validation
Updated validation rules in `constants/index.ts`:
```typescript
PHONE: {
  // Zimbabwe phone number pattern: +263 7X XXX XXXX or 07X XXX XXXX
  PATTERN: /^(\+263|263)?[7][1-8][0-9]{7}$/,
  MESSAGE: 'Please enter a valid Zimbabwe phone number (e.g., +263 77 123 4567 or 077 123 4567)'
}
```

## 3. Usage Instructions

### 3.1 Setting Up Twilio

1. **Get Twilio Credentials**:
   - Sign up at [Twilio.com](https://www.twilio.com)
   - Get Account SID and Auth Token
   - Set up WhatsApp Business API

2. **Configure Environment**:
   ```properties
   twilio.enabled=true
   twilio.account.sid=your_actual_account_sid
   twilio.auth.token=your_actual_auth_token
   twilio.whatsapp.from=whatsapp:+14155238886
   ```

3. **Test Configuration**:
   - Mark a student absent
   - Check logs for WhatsApp message status
   - Verify guardian receives notification

### 3.2 Updating Attendance Statistics

#### For Individual Reports
```typescript
await reportService.updateReportAttendance(reportId, "Term 1", "2025");
```

#### For Class Reports
```typescript
await reportService.updateClassAttendance("Form 1", "Green", "Term 1", "2025");
```

### 3.3 Viewing Attendance in Reports

Attendance statistics will appear in reports with:
- **Days Present**: Number of days student attended
- **Total School Days**: Total school days in the term
- **Attendance Percentage**: Calculated attendance rate

## 4. Technical Details

### 4.1 Dependencies Added

#### Backend (pom.xml)
```xml
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.14.1</version>
</dependency>
```

#### Frontend
- Updated validation patterns for Zimbabwe phone numbers
- Added attendance statistics interfaces
- Enhanced report service with attendance update methods

### 4.2 Database Schema

The `reports` table includes:
- `attendance_days`: INTEGER - Days student was present
- `total_school_days`: INTEGER - Total school days in term

### 4.3 Error Handling

- **Twilio failures**: Logged but don't prevent attendance marking
- **Invalid phone numbers**: Logged with warnings
- **Missing credentials**: System continues without WhatsApp notifications
- **Date validation**: Handles future dates gracefully

## 5. Future Enhancements

### 5.1 Attendance Features
- **Holiday calendar integration**: Exclude holidays from school days
- **Attendance trends**: Track attendance patterns over time
- **Automated reports**: Generate attendance reports automatically

### 5.2 WhatsApp Features
- **Message templates**: Customizable message templates
- **Bulk notifications**: Send notifications to multiple guardians
- **Delivery status**: Track message delivery status
- **SMS fallback**: Send SMS if WhatsApp fails

### 5.3 Integration Features
- **Guardian portal**: Allow guardians to view attendance online
- **Attendance alerts**: Proactive notifications for poor attendance
- **Analytics dashboard**: Attendance analytics and reporting

## 6. Testing

### 6.1 Manual Testing
1. Mark a student absent
2. Check WhatsApp notification is sent
3. Update attendance statistics for reports
4. Verify attendance data appears in reports

### 6.2 Automated Testing
- Unit tests for attendance calculation
- Integration tests for WhatsApp service
- Validation tests for phone number formats

## 7. Troubleshooting

### 7.1 Common Issues

#### WhatsApp Notifications Not Sending
- Check Twilio credentials are correct
- Verify `twilio.enabled=true`
- Check phone number format is valid
- Review application logs for errors

#### Attendance Statistics Not Updating
- Verify term dates are correct
- Check student has attendance records
- Ensure report exists for the student/term/year

#### Phone Number Validation Failing
- Verify number follows Zimbabwe format
- Check for extra spaces or special characters
- Ensure number starts with 7 and is 9 digits

### 7.2 Logs to Monitor
- `WhatsAppService` logs for message delivery
- `AttendanceService` logs for calculation errors
- `ReportService` logs for attendance updates

## 8. Security Considerations

- **Twilio credentials**: Store securely in environment variables
- **Phone number privacy**: Log only partial numbers for debugging
- **Access control**: Only authorized users can update attendance
- **Data validation**: Validate all input data before processing

---

This implementation provides a robust foundation for attendance tracking and parent communication, with room for future enhancements and customization.
