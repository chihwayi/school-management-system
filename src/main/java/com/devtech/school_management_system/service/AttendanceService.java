package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Attendance;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.AttendanceRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.GuardianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;
    private final WhatsAppService whatsAppService;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             GuardianRepository guardianRepository,
                             WhatsAppService whatsAppService) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.guardianRepository = guardianRepository;
        this.whatsAppService = whatsAppService;
    }

    public Attendance markAttendance(Long studentId, LocalDate date, boolean present) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Check if attendance already exists for this student and date
        Optional<Attendance> existingAttendance = attendanceRepository.findByStudentAndDate(student, date);

        Attendance attendance;
        if (existingAttendance.isPresent()) {
            attendance = existingAttendance.get();
            attendance.setPresent(present);
        } else {
            attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDate(date);
            attendance.setPresent(present);
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);

        // Send WhatsApp notification if student is absent
        if (!present) {
            sendAbsenteeNotification(student, date);
        }

        return savedAttendance;
    }

    public List<Attendance> getAttendanceByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return attendanceRepository.findByStudent(student);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public List<Attendance> getAttendanceByStudentAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return attendanceRepository.findByStudentAndDateBetween(student, startDate, endDate);
    }

    public List<Attendance> getAttendanceByClassAndDate(String form, String section, LocalDate date) {
        return attendanceRepository.findByStudentFormAndStudentSectionAndDate(form, section, date);
    }

    public double getAttendancePercentage(Long studentId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendanceRecords = getAttendanceByStudentAndDateRange(studentId, startDate, endDate);

        if (attendanceRecords.isEmpty()) {
            return 0.0;
        }

        long presentDays = attendanceRecords.stream()
                .mapToLong(attendance -> attendance.isPresent() ? 1 : 0)
                .sum();

        return (double) presentDays / attendanceRecords.size() * 100;
    }

    public void markClassAttendance(String form, String section, LocalDate date, List<Long> presentStudentIds) {
        List<Student> classStudents = studentRepository.findByFormAndSection(form, section);

        for (Student student : classStudents) {
            boolean present = presentStudentIds.contains(student.getId());
            markAttendance(student.getId(), date, present);
        }
    }

    private void sendAbsenteeNotification(Student student, LocalDate date) {
        try {
            List<Guardian> guardians = guardianRepository.findByStudentId(student.getId());
            Guardian primaryGuardian = guardians.stream()
                    .filter(Guardian::isPrimaryGuardian)
                    .findFirst()
                    .orElse(guardians.isEmpty() ? null : guardians.getFirst());

            if (primaryGuardian != null && primaryGuardian.getWhatsappNumber() != null) {
                String message = String.format(
                        "Dear %s,\n\nYour child %s %s was absent from school on %s. Please contact the school if this is unexpected.\n\nRegards,\nSchool Administration",
                        primaryGuardian.getName(),
                        student.getFirstName(),
                        student.getLastName(),
                        date.toString()
                );

                whatsAppService.sendAbsenteeNotification(student, guardians);
            }
        } catch (Exception e) {
            // Log error but don't fail the attendance marking
            System.err.println("Failed to send WhatsApp notification: " + e.getMessage());
        }
    }

    public List<Attendance> getAbsentStudentsForDate(LocalDate date) {
        return attendanceRepository.findByDateAndPresentFalse(date);
    }

    public List<Attendance> getAttendanceByClassAndDateRange(String form, String section, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByStudentFormAndStudentSectionAndDateBetween(form, section, startDate, endDate);
    }
}