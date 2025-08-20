package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Attendance;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.dto.AttendanceDTO;
import com.devtech.school_management_system.dto.AttendanceStatistics;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.AttendanceRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.GuardianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        
        // Set the markedBy field using the currently authenticated user
        // This is needed to show who marked the attendance
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()).getUsername();
            attendance.setMarkedBy(username);
        } else if (auth != null) {
            attendance.setMarkedBy(auth.getName());
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

    public List<AttendanceDTO> getAttendanceByDate(LocalDate date) {
        // Check if the date is in the future
        if (date.isAfter(LocalDate.now())) {
            // Return empty list for future dates instead of throwing an error
            return List.of();
        }
        
        List<Attendance> attendanceList = attendanceRepository.findByDate(date);
        return attendanceList.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        return new AttendanceDTO(
                attendance.getId(),
                attendance.getStudent().getId(),
                attendance.getStudent().getFirstName(),
                attendance.getStudent().getLastName(),
                attendance.getStudent().getStudentId(),
                attendance.getStudent().getForm(),
                attendance.getStudent().getSection(),
                attendance.getDate(),
                attendance.isPresent(),
                attendance.getMarkedBy(),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt()
        );
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

    /**
     * Calculate attendance statistics for a student within a term
     */
    public AttendanceStatistics calculateTermAttendance(Long studentId, String term, String academicYear) {
        // Define term date ranges (you may need to adjust these based on your school calendar)
        LocalDate startDate = getTermStartDate(term, academicYear);
        LocalDate endDate = getTermEndDate(term, academicYear);
        
        List<Attendance> attendanceRecords = getAttendanceByStudentAndDateRange(studentId, startDate, endDate);
        
        int totalSchoolDays = calculateTotalSchoolDays(startDate, endDate);
        int presentDays = (int) attendanceRecords.stream()
                .filter(Attendance::isPresent)
                .count();
        int absentDays = (int) attendanceRecords.stream()
                .filter(attendance -> !attendance.isPresent())
                .count();
        
        double attendancePercentage = totalSchoolDays > 0 ? (double) presentDays / totalSchoolDays * 100 : 0.0;
        
        return new AttendanceStatistics(presentDays, absentDays, totalSchoolDays, attendancePercentage);
    }

    /**
     * Get attendance statistics for all students in a class for a term
     */
    public Map<Long, AttendanceStatistics> getClassTermAttendance(String form, String section, String term, String academicYear) {
        LocalDate startDate = getTermStartDate(term, academicYear);
        LocalDate endDate = getTermEndDate(term, academicYear);
        
        List<Student> students = studentRepository.findByFormAndSection(form, section);
        Map<Long, AttendanceStatistics> statistics = new HashMap<>();
        
        for (Student student : students) {
            statistics.put(student.getId(), calculateTermAttendance(student.getId(), term, academicYear));
        }
        
        return statistics;
    }

    /**
     * Calculate total school days (excluding weekends and holidays)
     */
    private int calculateTotalSchoolDays(LocalDate startDate, LocalDate endDate) {
        int schoolDays = 0;
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            // Exclude weekends (Saturday = 6, Sunday = 7)
            if (current.getDayOfWeek().getValue() < 6) {
                schoolDays++;
            }
            current = current.plusDays(1);
        }
        
        return schoolDays;
    }

    /**
     * Get term start date based on term and academic year
     */
    private LocalDate getTermStartDate(String term, String academicYear) {
        int year = Integer.parseInt(academicYear);
        
        switch (term.toLowerCase()) {
            case "term 1":
                return LocalDate.of(year, 1, 15); // Adjust based on your school calendar
            case "term 2":
                return LocalDate.of(year, 5, 1);  // Adjust based on your school calendar
            case "term 3":
                return LocalDate.of(year, 9, 1);  // Adjust based on your school calendar
            default:
                return LocalDate.of(year, 1, 1);
        }
    }

    /**
     * Get term end date based on term and academic year
     */
    private LocalDate getTermEndDate(String term, String academicYear) {
        int year = Integer.parseInt(academicYear);
        
        switch (term.toLowerCase()) {
            case "term 1":
                return LocalDate.of(year, 4, 30); // Adjust based on your school calendar
            case "term 2":
                return LocalDate.of(year, 8, 31); // Adjust based on your school calendar
            case "term 3":
                return LocalDate.of(year, 12, 31); // Adjust based on your school calendar
            default:
                return LocalDate.of(year, 12, 31);
        }
    }
}