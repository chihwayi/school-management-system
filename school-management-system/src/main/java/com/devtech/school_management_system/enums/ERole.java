package com.devtech.school_management_system.enums;

public enum ERole {
    ROLE_USER,
    ROLE_ADMIN,      // Can do everything in the system
    ROLE_CLERK,      // Can manage teachers, students, class assignments
    ROLE_TEACHER,    // Can mark attendance and record grades for assigned subjects/classes
    ROLE_CLASS_TEACHER  // Teacher with additional class oversight responsibilities
}
