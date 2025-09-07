package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.entity.*;
import com.devtech.school_management_system.repository.*;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final StudentRepository studentRepository;
    private final AssessmentRepository assessmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final ReportRepository reportRepository;
    private final SubjectReportRepository subjectReportRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public ReportService(StudentRepository studentRepository,
                        AssessmentRepository assessmentRepository,
                        TeacherRepository teacherRepository,
                        StudentSubjectRepository studentSubjectRepository,
                        ReportRepository reportRepository,
                        SubjectReportRepository subjectReportRepository,
                        SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.assessmentRepository = assessmentRepository;
        this.teacherRepository = teacherRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.reportRepository = reportRepository;
        this.subjectReportRepository = subjectReportRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<StudentReportDTO> getClassReports(String form, String section, String term, String year, String username) {
        // Get all students in the class
        List<Student> students = studentRepository.findByFormAndSectionAndYear(form, section, year);
        
        return students.stream()
                .map(student -> createStudentReportDTO(student, term, year))
                .collect(Collectors.toList());
    }

    public List<StudentReportDTO> getSubjectReports(Long subjectId, String form, String section, String term, String year, String username) {
        System.out.println("=== getSubjectReports called: subjectId=" + subjectId + ", form=" + form + ", section=" + section + ", term=" + term + ", year=" + year + " ===");
        
        // Get all students in the class
        List<Student> students = studentRepository.findByFormAndSectionAndYear(form, section, year);
        System.out.println("=== Found " + students.size() + " students ===");
        
        return students.stream()
                .map(student -> {
                    System.out.println("=== Processing student: " + student.getFirstName() + " " + student.getLastName() + " ===");
                    return createStudentReportDTOForSubject(student, term, year, subjectId);
                })
                .collect(Collectors.toList());
    }

    private StudentReportDTO createStudentReportDTO(Student student, String term, String year) {
        
        // Find or create report for this student, term, and year
        Report report = reportRepository.findByStudentIdAndTermAndAcademicYear(student.getId(), term, year)
                .orElseGet(() -> {
                    Report newReport = new Report();
                    newReport.setStudent(student);
                    newReport.setTerm(term);
                    newReport.setAcademicYear(year);
                    newReport.setFinalized(false);
                    return reportRepository.save(newReport);
                });
        
        StudentReportDTO dto = new StudentReportDTO();
        dto.setId(report.getId()); // Set the actual report ID
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        dto.setForm(student.getForm());
        dto.setSection(student.getSection());
        dto.setTerm(term);
        dto.setAcademicYear(year);
        dto.setFinalized(report.isFinalized());
        dto.setOverallComment(report.getOverallComment());
        dto.setClassTeacherSignatureUrl(report.getClassTeacherSignatureUrl());
        
        // Find existing report for this student, term, and year
        Report existingReport = reportRepository.findByStudentIdAndTermAndAcademicYear(student.getId(), term, year)
                .orElse(null);
        
        
        List<SubjectReportDTO> subjectReports;
        
        if (existingReport != null && !existingReport.getSubjectReports().isEmpty()) {
            // Use existing subject reports with comments
            subjectReports = existingReport.getSubjectReports().stream()
                    .map(subjectReport -> {
                        SubjectReportDTO subjectDto = new SubjectReportDTO();
                        subjectDto.setId(subjectReport.getId());
                        subjectDto.setSubjectId(subjectReport.getSubject().getId());
                        subjectDto.setSubjectName(subjectReport.getSubject().getName());
                        subjectDto.setSubjectCode(subjectReport.getSubject().getCode());
                        subjectDto.setCourseworkMark(subjectReport.getCourseworkMark());
                        subjectDto.setExamMark(subjectReport.getExamMark());
                        subjectDto.setFinalMark(subjectReport.getTotalMark());
                        subjectDto.setComment(subjectReport.getTeacherComment()); // Include teacher comment
                        subjectDto.setTeacherId(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getId() : null);
                        subjectDto.setTeacherName(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getFirstName() + " " + subjectReport.getTeacher().getLastName() : null);
                        subjectDto.setTeacherSignatureUrl(subjectReport.getTeacherSignatureUrl());
                        return subjectDto;
                    })
                    .collect(Collectors.toList());
        } else {
            // Fallback to StudentSubject relationships if no report exists
            List<StudentSubject> studentSubjects = studentSubjectRepository.findByStudentId(student.getId());
            subjectReports = studentSubjects.stream()
                    .map(studentSubject -> {
                        Subject subject = studentSubject.getSubject();
                        SubjectReportDTO subjectDto = new SubjectReportDTO();
                        subjectDto.setSubjectId(subject.getId());
                        subjectDto.setSubjectName(subject.getName());
                        subjectDto.setSubjectCode(subject.getCode());
                        
                        // Calculate marks from assessments
                        try {
                            // Try the original method first
                            List<Assessment> assessments = assessmentRepository.findByStudentSubjectTermAndYear(
                                    student.getId(), subject.getId(), term, year);
                            
                            // If no assessments found, try alternative approach
                            if (assessments.isEmpty()) {
                                List<Assessment> allStudentAssessments = assessmentRepository.findByStudentIdAndTermAndAcademicYear(
                                        student.getId(), term, year);
                                System.out.println("=== Found " + allStudentAssessments.size() + " total assessments for student " + student.getFirstName() + " in " + term + " " + year + " ===");
                                
                                // Debug: Log details of each assessment found
                                for (Assessment assessment : allStudentAssessments) {
                                    System.out.println("=== Assessment - ID: " + assessment.getId() + 
                                              ", Title: " + assessment.getTitle() + 
                                              ", Term: " + assessment.getTerm() + 
                                              ", Year: " + assessment.getAcademicYear() + 
                                              ", StudentSubject: " + (assessment.getStudentSubject() != null ? assessment.getStudentSubject().getId() : "null") +
                                              ", Subject: " + (assessment.getStudentSubject() != null && assessment.getStudentSubject().getSubject() != null ? assessment.getStudentSubject().getSubject().getName() + " (ID: " + assessment.getStudentSubject().getSubject().getId() + ")" : "null"));
                                }
                                
                                // Filter by subject ID manually
                                assessments = allStudentAssessments.stream()
                                        .filter(assessment -> assessment.getStudentSubject() != null && 
                                                assessment.getStudentSubject().getSubject().getId().equals(subject.getId()))
                                        .collect(Collectors.toList());
                                System.out.println("=== After filtering by subject " + subject.getName() + " (ID: " + subject.getId() + "), found " + assessments.size() + " assessments ===");
                            }
                            
                            double courseworkTotal = 0, examTotal = 0;
                            int courseworkCount = 0, examCount = 0;
                            
                            for (Assessment assessment : assessments) {
                                double percentage = (assessment.getScore() / assessment.getMaxScore()) * 100;
                                if ("COURSEWORK".equals(assessment.getType().name())) {
                                    courseworkTotal += percentage;
                                    courseworkCount++;
                                } else if ("FINAL_EXAM".equals(assessment.getType().name())) {
                                    examTotal += percentage;
                                    examCount++;
                                }
                            }
                            
                            if (courseworkCount > 0) {
                                subjectDto.setCourseworkMark(courseworkTotal / courseworkCount);
                            }
                            if (examCount > 0) {
                                subjectDto.setExamMark(examTotal / examCount);
                            }
                            
                            // Don't calculate a final mark - show individual marks as is
                            // The teacher will provide a summary comment based on these individual marks
                            subjectDto.setFinalMark(null);
                        } catch (Exception e) {
                            // If there's an error calculating marks, set to null
                            subjectDto.setFinalMark(null);
                        }
                        
                        return subjectDto;
                    })
                    .collect(Collectors.toList());
        }
        
        dto.setSubjectReports(subjectReports);
        return dto;
    }

    private StudentReportDTO createStudentReportDTOForSubject(Student student, String term, String year, Long subjectId) {
        System.out.println("=== createStudentReportDTOForSubject: student=" + student.getFirstName() + " " + student.getLastName() + ", subjectId=" + subjectId + ", term=" + term + ", year=" + year + " ===");
        
        // Find or create report for this student, term, and year
        Report report = reportRepository.findByStudentIdAndTermAndAcademicYear(student.getId(), term, year)
                .orElseGet(() -> {
                    Report newReport = new Report();
                    newReport.setStudent(student);
                    newReport.setTerm(term);
                    newReport.setAcademicYear(year);
                    newReport.setFinalized(false);
                    return reportRepository.save(newReport);
                });
        
        StudentReportDTO dto = new StudentReportDTO();
        dto.setId(report.getId()); // Set the actual report ID
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        dto.setForm(student.getForm());
        dto.setSection(student.getSection());
        dto.setTerm(term);
        dto.setAcademicYear(year);
        dto.setFinalized(report.isFinalized());
        dto.setOverallComment(report.getOverallComment());
        dto.setClassTeacherSignatureUrl(report.getClassTeacherSignatureUrl());
        
        // Find existing report for this student, term, and year
        Report existingReport = reportRepository.findByStudentIdAndTermAndAcademicYear(student.getId(), term, year)
                .orElse(null);
        
        List<SubjectReportDTO> subjectReports;
        
        if (existingReport != null && !existingReport.getSubjectReports().isEmpty()) {
            // Use existing subject reports with comments, but filter to only the requested subject
            subjectReports = existingReport.getSubjectReports().stream()
                    .filter(subjectReport -> subjectReport.getSubject().getId().equals(subjectId))
                    .map(subjectReport -> {
                        SubjectReportDTO subjectDto = new SubjectReportDTO();
                        subjectDto.setId(subjectReport.getId());
                        subjectDto.setSubjectId(subjectReport.getSubject().getId());
                        subjectDto.setSubjectName(subjectReport.getSubject().getName());
                        subjectDto.setSubjectCode(subjectReport.getSubject().getCode());
                        subjectDto.setCourseworkMark(subjectReport.getCourseworkMark());
                        subjectDto.setExamMark(subjectReport.getExamMark());
                        subjectDto.setFinalMark(subjectReport.getTotalMark());
                        subjectDto.setComment(subjectReport.getTeacherComment()); // Include teacher comment
                        subjectDto.setTeacherId(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getId() : null);
                        subjectDto.setTeacherName(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getFirstName() + " " + subjectReport.getTeacher().getLastName() : null);
                        subjectDto.setTeacherSignatureUrl(subjectReport.getTeacherSignatureUrl());
                        return subjectDto;
                    })
                    .collect(Collectors.toList());
        } else {
            // Fallback: Create subject report for the requested subject even if no StudentSubject relationship exists
            Subject subject = subjectRepository.findById(subjectId).orElse(null);
            if (subject != null) {
                SubjectReportDTO subjectDto = new SubjectReportDTO();
                subjectDto.setSubjectId(subject.getId());
                subjectDto.setSubjectName(subject.getName());
                subjectDto.setSubjectCode(subject.getCode());
                
                // Calculate marks from assessments
                try {
                    // Get all assessments for this student in the term/year and filter by subject
                    List<Assessment> allStudentAssessments = assessmentRepository.findByStudentIdAndTermAndAcademicYear(
                            student.getId(), term, year);
                    System.out.println("=== Found " + allStudentAssessments.size() + " total assessments for student " + student.getFirstName() + " in " + term + " " + year + " ===");
                    
                    // Filter by subject ID manually
                    List<Assessment> assessments = allStudentAssessments.stream()
                            .filter(assessment -> assessment.getStudentSubject() != null && 
                                    assessment.getStudentSubject().getSubject().getId().equals(subject.getId()))
                            .collect(Collectors.toList());
                    System.out.println("=== After filtering by subject " + subject.getName() + " (ID: " + subject.getId() + "), found " + assessments.size() + " assessments ===");
                    
                    double courseworkTotal = 0, examTotal = 0;
                    int courseworkCount = 0, examCount = 0;
                    
                    for (Assessment assessment : assessments) {
                        double percentage = (assessment.getScore() / assessment.getMaxScore()) * 100;
                        if ("COURSEWORK".equals(assessment.getType().name())) {
                            courseworkTotal += percentage;
                            courseworkCount++;
                        } else if ("FINAL_EXAM".equals(assessment.getType().name())) {
                            examTotal += percentage;
                            examCount++;
                        }
                    }
                    
                    if (courseworkCount > 0) {
                        subjectDto.setCourseworkMark(courseworkTotal / courseworkCount);
                    }
                    if (examCount > 0) {
                        subjectDto.setExamMark(examTotal / examCount);
                    }
                    
                    // Don't calculate a final mark - show individual marks as is
                    // The teacher will provide a summary comment based on these individual marks
                    subjectDto.setFinalMark(null);
                } catch (Exception e) {
                    // If there's an error calculating marks, set to null
                    subjectDto.setFinalMark(null);
                }
                
                subjectReports = List.of(subjectDto);
            } else {
                subjectReports = new ArrayList<>();
            }
        }
        
        dto.setSubjectReports(subjectReports);
        return dto;
    }

    public void addSubjectComment(SubjectCommentDTO commentDTO, String username) {
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        // Find or create report
        Report report = findOrCreateReport(commentDTO.getReportId(), teacher);
        
        // Find or create subject report
        SubjectReport subjectReport = report.getSubjectReports().stream()
                .filter(sr -> sr.getSubject().getId().equals(commentDTO.getSubjectId()))
                .findFirst()
                .orElseGet(() -> {
                    Subject subject = subjectRepository.findById(commentDTO.getSubjectId())
                            .orElseThrow(() -> new RuntimeException("Subject not found"));
                    
                    SubjectReport newSubjectReport = new SubjectReport();
                    newSubjectReport.setReport(report);
                    newSubjectReport.setSubject(subject);
                    
                    // Calculate and set marks from assessments
                    try {
                        List<Assessment> assessments = assessmentRepository.findByStudentSubjectTermAndYear(
                                report.getStudent().getId(), subject.getId(), report.getTerm(), report.getAcademicYear());
                        
                        double courseworkTotal = 0, examTotal = 0;
                        int courseworkCount = 0, examCount = 0;
                        
                        for (Assessment assessment : assessments) {
                            double percentage = (assessment.getScore() / assessment.getMaxScore()) * 100;
                            if ("COURSEWORK".equals(assessment.getType().name())) {
                                courseworkTotal += percentage;
                                courseworkCount++;
                            } else if ("FINAL_EXAM".equals(assessment.getType().name())) {
                                examTotal += percentage;
                                examCount++;
                            }
                        }
                        
                        if (courseworkCount > 0) {
                            newSubjectReport.setCourseworkMark(courseworkTotal / courseworkCount);
                        }
                        if (examCount > 0) {
                            newSubjectReport.setExamMark(examTotal / examCount);
                        }
                        
                        // Don't calculate a total mark - show individual marks as is
                        // The teacher will provide a summary comment based on these individual marks
                        newSubjectReport.setTotalMark(null);
                        
                        // Don't set a grade automatically - let teacher provide summary comment
                        newSubjectReport.setGrade(null);
                        
                    } catch (Exception e) {
                        // If error calculating marks, set defaults
                        newSubjectReport.setCourseworkMark(0.0);
                        newSubjectReport.setExamMark(0.0);
                        newSubjectReport.setTotalMark(null);
                        newSubjectReport.setGrade(null);
                    }
                    
                    report.getSubjectReports().add(newSubjectReport);
                    return newSubjectReport;
                });
        
        subjectReport.setTeacherComment(commentDTO.getComment());
        subjectReport.setTeacher(teacher);
        reportRepository.save(report);
    }

    public void addOverallComment(OverallCommentDTO commentDTO, String username) {
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        Report report = findOrCreateReport(commentDTO.getReportId(), teacher);
        report.setOverallComment(commentDTO.getComment());
        report.setClassTeacher(teacher);
        reportRepository.save(report);
    }

    public void finalizeReport(Long reportId, String username) {
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        Report report = findOrCreateReport(reportId, teacher);
        report.setFinalized(true);
        Report savedReport = reportRepository.save(report);
        System.out.println("Report finalized - ID: " + savedReport.getId() + ", Finalized: " + savedReport.isFinalized());
    }
    
    private Report findOrCreateReport(Long reportId, Teacher teacher) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));
    }

    public String testAssessments(Long studentId, Long subjectId, String term, String year) {
        StringBuilder result = new StringBuilder();
        result.append("=== TEST ASSESSMENTS ===\n");
        result.append("Student ID: ").append(studentId).append("\n");
        result.append("Subject ID: ").append(subjectId).append("\n");
        result.append("Term: ").append(term).append("\n");
        result.append("Year: ").append(year).append("\n\n");

        // Test 1: Find assessments by StudentSubject relationship
        List<Assessment> assessments1 = assessmentRepository.findByStudentSubjectTermAndYear(studentId, subjectId, term, year);
        result.append("Method 1 (findByStudentSubjectTermAndYear): ").append(assessments1.size()).append(" assessments\n");
        for (Assessment a : assessments1) {
            result.append("  - ").append(a.getTitle()).append(" (").append(a.getType()).append("): ").append(a.getScore()).append("/").append(a.getMaxScore()).append("\n");
        }

        // Test 2: Find all assessments for student and filter manually
        List<Assessment> allAssessments = assessmentRepository.findByStudentIdAndTermAndAcademicYear(studentId, term, year);
        result.append("\nMethod 2 (findByStudentIdAndTermAndAcademicYear): ").append(allAssessments.size()).append(" total assessments\n");
        for (Assessment a : allAssessments) {
            result.append("  - ").append(a.getTitle()).append(" (").append(a.getType()).append("): ").append(a.getScore()).append("/").append(a.getMaxScore());
            if (a.getStudentSubject() != null && a.getStudentSubject().getSubject() != null) {
                result.append(" [Subject: ").append(a.getStudentSubject().getSubject().getName()).append(" (ID: ").append(a.getStudentSubject().getSubject().getId()).append(")]");
            }
            result.append("\n");
        }

        // Test 3: Filter by subject ID
        List<Assessment> filteredAssessments = allAssessments.stream()
                .filter(assessment -> assessment.getStudentSubject() != null && 
                        assessment.getStudentSubject().getSubject().getId().equals(subjectId))
                .collect(Collectors.toList());
        result.append("\nMethod 3 (Filtered by subject ID ").append(subjectId).append("): ").append(filteredAssessments.size()).append(" assessments\n");
        for (Assessment a : filteredAssessments) {
            result.append("  - ").append(a.getTitle()).append(" (").append(a.getType()).append("): ").append(a.getScore()).append("/").append(a.getMaxScore()).append("\n");
        }

        return result.toString();
    }

    // Get all reports for a specific student
    public List<StudentReportDTO> getReportsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        List<Report> reports = reportRepository.findByStudentId(studentId);
        
        return reports.stream()
                .map(report -> {
                    StudentReportDTO dto = new StudentReportDTO();
                    dto.setId(report.getId());
                    dto.setStudentId(studentId);
                    dto.setStudentName(student.getFirstName() + " " + student.getLastName());
                    dto.setForm(student.getForm());
                    dto.setSection(student.getSection());
                    dto.setTerm(report.getTerm());
                    dto.setAcademicYear(report.getAcademicYear());
                    dto.setFinalized(report.isFinalized());
                    dto.setOverallComment(report.getOverallComment());
                    dto.setClassTeacherSignatureUrl(report.getClassTeacherSignatureUrl());
                    
                    // Get subject reports for this report
                    List<SubjectReport> subjectReports = subjectReportRepository.findByReportId(report.getId());
                    List<SubjectReportDTO> subjectReportDTOs = subjectReports.stream()
                            .map(this::convertToSubjectReportDTO)
                            .collect(Collectors.toList());
                    dto.setSubjectReports(subjectReportDTOs);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Get a specific report by ID
    public StudentReportDTO getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));
        
        Student student = report.getStudent();
        
        StudentReportDTO dto = new StudentReportDTO();
        dto.setId(report.getId());
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        dto.setForm(student.getForm());
        dto.setSection(student.getSection());
        dto.setTerm(report.getTerm());
        dto.setAcademicYear(report.getAcademicYear());
        dto.setFinalized(report.isFinalized());
        dto.setOverallComment(report.getOverallComment());
        dto.setClassTeacherSignatureUrl(report.getClassTeacherSignatureUrl());
        
        // Get subject reports for this report
        List<SubjectReport> subjectReports = subjectReportRepository.findByReportId(report.getId());
        List<SubjectReportDTO> subjectReportDTOs = subjectReports.stream()
                .map(this::convertToSubjectReportDTO)
                .collect(Collectors.toList());
        dto.setSubjectReports(subjectReportDTOs);
        
        return dto;
    }

    // Generate PDF URL for a report (placeholder implementation)
    public String generateReportPdf(Long reportId) {
        // For now, return a placeholder URL that can be viewed in browser
        // In a real implementation, this would generate an actual PDF file
        return "data:application/pdf;base64,JVBERi0xLjQKJcOkw7zDtsO8CjIgMCBvYmoKPDwKL0xlbmd0aCAzIDAgUgovVHlwZSAvUGFnZQo+PgpzdHJlYW0KJVBERi0xLjQKJcOkw7zDtsO8CjIgMCBvYmoKPDwKL0xlbmd0aCAzIDAgUgovVHlwZSAvUGFnZQo+PgpzdHJlYW0K";
    }

    private SubjectReportDTO convertToSubjectReportDTO(SubjectReport subjectReport) {
        SubjectReportDTO dto = new SubjectReportDTO();
        dto.setId(subjectReport.getId());
        dto.setSubjectId(subjectReport.getSubject().getId());
        dto.setSubjectName(subjectReport.getSubject().getName());
        dto.setSubjectCode(subjectReport.getSubject().getCode());
        dto.setFinalMark(subjectReport.getTotalMark());
        dto.setComment(subjectReport.getTeacherComment());
        dto.setTeacherSignatureUrl(subjectReport.getTeacherSignatureUrl());
        
        // Set coursework and exam marks from the subject report
        dto.setCourseworkMark(subjectReport.getCourseworkMark());
        dto.setExamMark(subjectReport.getExamMark());
        
        return dto;
    }
}