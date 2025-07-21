package com.devtech.school_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "student_id", unique = true, nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String form;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String level; // O_LEVEL or A_LEVEL

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<StudentSubject> studentSubjects = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Guardian> guardians = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Attendance> attendanceRecords = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Report> reports = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public Student() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Set<StudentSubject> getStudentSubjects() { return studentSubjects; }
    public void setStudentSubjects(Set<StudentSubject> studentSubjects) { this.studentSubjects = studentSubjects; }

    public Set<Guardian> getGuardians() { return guardians; }
    public void setGuardians(Set<Guardian> guardians) { this.guardians = guardians; }

    public Set<Attendance> getAttendanceRecords() { return attendanceRecords; }
    public void setAttendanceRecords(Set<Attendance> attendanceRecords) { this.attendanceRecords = attendanceRecords; }

    public Set<Report> getReports() { return reports; }
    public void setReports(Set<Report> reports) { this.reports = reports; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getClassName() {
        return form + " " + section;
    }
}
