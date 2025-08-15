package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.StudentUpdateDTO;
import com.devtech.school_management_system.dto.StudentSubjectAssignmentDTO;
import com.devtech.school_management_system.dto.StudentRegistrationDTO;
import com.devtech.school_management_system.dto.GuardianDTO;
import com.devtech.school_management_system.entity.*;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassGroupRepository classGroupRepository;
    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final GuardianRepository guardianRepository;
    private final AssessmentRepository assessmentRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final ReportRepository reportRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          ClassGroupRepository classGroupRepository,
                          SubjectRepository subjectRepository,
                          StudentSubjectRepository studentSubjectRepository,
                          GuardianRepository guardianRepository,
                          AssessmentRepository assessmentRepository,
                          FeePaymentRepository feePaymentRepository,
                          ReportRepository reportRepository,
                          AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.classGroupRepository = classGroupRepository;
        this.subjectRepository = subjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.guardianRepository = guardianRepository;
        this.assessmentRepository = assessmentRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.reportRepository = reportRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student createStudent(String firstName, String lastName, String studentId,
                                 String form, String section, String level, String academicYear) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        student.setForm(form);
        student.setSection(section);
        student.setLevel(level);
        
        // Standardize academic year format
        if (academicYear != null && academicYear.contains("-")) {
            // If format is "2024-2025", use just the end year "2025"
            String[] parts = academicYear.split("-");
            if (parts.length > 1) {
                academicYear = parts[1].trim();
            }
        }
        student.setAcademicYear(academicYear);
        
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        return studentRepository.save(student);
    }

    public Student createStudentWithGuardians(StudentRegistrationDTO registrationDTO) {
        // Create the student first
        Student student = createStudent(
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getStudentId(),
                registrationDTO.getForm(),
                registrationDTO.getSection(),
                registrationDTO.getLevel(),
                registrationDTO.getAcademicYear()
        );

        // Create guardians if provided
        if (registrationDTO.getGuardians() != null && !registrationDTO.getGuardians().isEmpty()) {
            for (GuardianDTO guardianDTO : registrationDTO.getGuardians()) {
                Guardian guardian = new Guardian();
                guardian.setName(guardianDTO.getName());
                guardian.setRelationship(guardianDTO.getRelationship());
                guardian.setPhoneNumber(guardianDTO.getPhoneNumber());
                guardian.setWhatsappNumber(guardianDTO.getWhatsappNumber());
                guardian.setPrimaryGuardian(guardianDTO.isPrimaryGuardian());
                guardian.setStudent(student);
                guardian.setCreatedAt(LocalDateTime.now());
                guardian.setUpdatedAt(LocalDateTime.now());
                
                guardianRepository.save(guardian);
            }
        }

        return student;
    }

    public Student updateStudent(Long id, StudentUpdateDTO updateDTO) {
        Student student = getStudentById(id);

        if (updateDTO.getFirstName() != null) {
            student.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            student.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getForm() != null) {
            student.setForm(updateDTO.getForm());
        }
        if (updateDTO.getSection() != null) {
            student.setSection(updateDTO.getSection());
        }
        if (updateDTO.getLevel() != null) {
            student.setLevel(updateDTO.getLevel());
        }

        student.setUpdatedAt(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        
        // Delete related records first to avoid foreign key constraint violations
        
        // Delete fee payments
        feePaymentRepository.deleteByStudentId(id);
        
        // Delete reports
        reportRepository.deleteByStudentId(id);
        
        // Delete attendance records
        attendanceRepository.deleteByStudentId(id);
        
        // Delete assessments (they depend on student-subject relationships)
        assessmentRepository.deleteByStudentId(id);
        
        // Delete student-subject assignments
        studentSubjectRepository.deleteByStudentId(id);
        
        // Delete guardians
        guardianRepository.deleteByStudentId(id);
        
        // Finally delete the student
        studentRepository.deleteById(id);
    }

    public List<Student> getStudentsByClass(String form, String section) {
        return studentRepository.findByFormAndSection(form, section);
    }

    public StudentSubject assignSubjectToStudent(Long studentId, Long subjectId) {
        Student student = getStudentById(studentId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        // Check if already assigned
        if (studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId).isPresent()) {
            throw new IllegalArgumentException("Subject already assigned to student");
        }

        StudentSubject studentSubject = new StudentSubject();
        studentSubject.setStudent(student);
        studentSubject.setSubject(subject);
        studentSubject.setAcademicYear(student.getAcademicYear());
        studentSubject.setAssignedDate(LocalDateTime.now());
        studentSubject.setCreatedAt(LocalDateTime.now());
        studentSubject.setUpdatedAt(LocalDateTime.now());

        return studentSubjectRepository.save(studentSubject);
    }

    public void removeSubjectFromStudent(Long studentId, Long subjectId) {
        StudentSubject studentSubject = studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Student-Subject assignment not found"));

        studentSubjectRepository.delete(studentSubject);
    }

    public List<Subject> getStudentSubjects(Long studentId) {
        return studentSubjectRepository.findByStudentId(studentId)
                .stream()
                .map(StudentSubject::getSubject)
                .collect(Collectors.toList());
    }

    public List<Student> advanceStudentsToNextForm(List<Long> studentIds) {
        List<Student> students = studentRepository.findAllById(studentIds);

        for (Student student : students) {
            String currentForm = student.getForm();
            String nextForm = getNextForm(currentForm);
            student.setForm(nextForm);
            student.setUpdatedAt(LocalDateTime.now());
        }

        return studentRepository.saveAll(students);
    }

    public List<Student> promoteStudentsToALevel(List<Long> studentIds, List<Long> subjectIds,
                                                 String form, String section) {
        List<Student> students = studentRepository.findAllById(studentIds);
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);

        for (Student student : students) {
            student.setForm(form);
            student.setSection(section);
            student.setLevel("A_LEVEL");
            student.setUpdatedAt(LocalDateTime.now());

            // Assign A-Level subjects
            for (Subject subject : subjects) {
                StudentSubject studentSubject = new StudentSubject();
                studentSubject.setStudent(student);
                studentSubject.setSubject(subject);
                studentSubject.setCreatedAt(LocalDateTime.now());
                studentSubject.setUpdatedAt(LocalDateTime.now());
                studentSubjectRepository.save(studentSubject);
            }
        }

        return studentRepository.saveAll(students);
    }

    @Transactional
    public void bulkAssignSubjectsToClass(String form, String section, List<Long> subjectIds) {
        List<Student> students = studentRepository.findByFormAndSection(form, section);
        
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students found in class " + form + " " + section);
        }
        
        for (Student student : students) {
            for (Long subjectId : subjectIds) {
                boolean alreadyAssigned = studentSubjectRepository
                        .findByStudentIdAndSubjectId(student.getId(), subjectId)
                        .isPresent();
                
                if (!alreadyAssigned) {
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));
                    
                    StudentSubject studentSubject = new StudentSubject();
                    studentSubject.setStudent(student);
                    studentSubject.setSubject(subject);
                    studentSubject.setAcademicYear(student.getAcademicYear());
                    studentSubject.setAssignedDate(LocalDateTime.now());
                    studentSubject.setCreatedAt(LocalDateTime.now());
                    studentSubject.setUpdatedAt(LocalDateTime.now());
                    
                    studentSubjectRepository.save(studentSubject);
                }
            }
        }
    }

    @Transactional
    public List<StudentSubject> hybridAssignSubjects(StudentSubjectAssignmentDTO assignmentDTO) {
        List<Student> targetStudents;
        
        // Determine target students based on assignment type
        switch (assignmentDTO.getAssignmentType()) {
            case SINGLE:
                if (assignmentDTO.getStudentIds() == null || assignmentDTO.getStudentIds().size() != 1) {
                    throw new IllegalArgumentException("Single assignment requires exactly one student ID");
                }
                targetStudents = studentRepository.findAllById(assignmentDTO.getStudentIds());
                break;
                
            case BULK_CLASS:
                if (assignmentDTO.getForm() == null || assignmentDTO.getSection() == null) {
                    throw new IllegalArgumentException("Bulk class assignment requires form and section");
                }
                targetStudents = studentRepository.findByFormAndSection(
                    assignmentDTO.getForm(), assignmentDTO.getSection());
                break;
                
            case BULK_CUSTOM:
                if (assignmentDTO.getStudentIds() == null || assignmentDTO.getStudentIds().isEmpty()) {
                    throw new IllegalArgumentException("Bulk custom assignment requires student IDs");
                }
                targetStudents = studentRepository.findAllById(assignmentDTO.getStudentIds());
                break;
                
            default:
                throw new IllegalArgumentException("Invalid assignment type");
        }
        
        if (targetStudents.isEmpty()) {
            throw new ResourceNotFoundException("No students found for assignment");
        }
        
        // Validate subjects exist
        List<Subject> subjects = subjectRepository.findAllById(assignmentDTO.getSubjectIds());
        if (subjects.size() != assignmentDTO.getSubjectIds().size()) {
            throw new ResourceNotFoundException("One or more subjects not found");
        }
        
        List<StudentSubject> assignments = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Create assignments
        for (Student student : targetStudents) {
            for (Subject subject : subjects) {
                // Check if already assigned
                if (!studentSubjectRepository.findByStudentIdAndSubjectId(
                        student.getId(), subject.getId()).isPresent()) {
                    
                    StudentSubject studentSubject = new StudentSubject();
                    studentSubject.setStudent(student);
                    studentSubject.setSubject(subject);
                    studentSubject.setAcademicYear(assignmentDTO.getAcademicYear() != null ? 
                        assignmentDTO.getAcademicYear() : student.getAcademicYear());
                    studentSubject.setAssignedDate(now);
                    studentSubject.setCreatedAt(now);
                    studentSubject.setUpdatedAt(now);
                    
                    assignments.add(studentSubjectRepository.save(studentSubject));
                }
            }
        }
        
        return assignments;
    }

    public StudentSubject getStudentSubjectRelationship(Long studentId, Long subjectId) {
        return studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Student-Subject relationship not found for student " + studentId + " and subject " + subjectId));
    }

    private String getNextForm(String currentForm) {
        switch (currentForm) {
            case "Form 1":
                return "Form 2";
            case "Form 2":
                return "Form 3";
            case "Form 3":
                return "Form 4";
            case "Form 4":
                return "Form 5";
            case "Form 5":
                return "Form 6";
            default:
                return currentForm;
        }
    }
}
