package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.dto.AttendanceStatistics;
import com.devtech.school_management_system.entity.*;
import com.devtech.school_management_system.repository.*;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    private final StudentRepository studentRepository;
    private final AssessmentRepository assessmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final ReportRepository reportRepository;
    private final SubjectReportRepository subjectReportRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceService attendanceService;

    @Autowired
    public ReportService(StudentRepository studentRepository,
                        AssessmentRepository assessmentRepository,
                        TeacherRepository teacherRepository,
                        StudentSubjectRepository studentSubjectRepository,
                        ReportRepository reportRepository,
                        SubjectReportRepository subjectReportRepository,
                        SubjectRepository subjectRepository,
                        AttendanceService attendanceService) {
        this.studentRepository = studentRepository;
        this.assessmentRepository = assessmentRepository;
        this.teacherRepository = teacherRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.reportRepository = reportRepository;
        this.subjectReportRepository = subjectReportRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceService = attendanceService;
    }

    public List<StudentReportDTO> getClassReports(String form, String section, String term, String year, String username) {
        // Get all students in the class
        List<Student> students = studentRepository.findByFormAndSectionAndYear(form, section, year);
        
        return students.stream()
                .map(student -> createStudentReportDTO(student, term, year, null)) // null for class reports (all subjects)
                .collect(Collectors.toList());
    }

    public List<StudentReportDTO> getSubjectReports(Long subjectId, String form, String section, String term, String year, String username) {
        System.out.println("getSubjectReports called with subjectId: " + subjectId + ", form: " + form + ", section: " + section + ", term: " + term + ", year: " + year);
        
        // Get all students in the class
        List<Student> students = studentRepository.findByFormAndSectionAndYear(form, section, year);
        System.out.println("Found " + students.size() + " students in " + form + " " + section);
        
        return students.stream()
                .map(student -> createStudentReportDTO(student, term, year, subjectId))
                .collect(Collectors.toList());
    }

    /**
     * Calculate grade based on mark
     */
    private String calculateGrade(double mark) {
        if (mark >= 75) return "A";
        else if (mark >= 60) return "B";
        else if (mark >= 50) return "C";
        else if (mark >= 40) return "D";
        else if (mark >= 30) return "E";
        else return "U";
    }

    private StudentReportDTO createStudentReportDTO(Student student, String term, String year, Long subjectId) {
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
        
        // Find existing report for this student, term, and year
        Report existingReport = reportRepository.findByStudentIdAndTermAndAcademicYear(student.getId(), term, year)
                .orElse(null);
        
        List<SubjectReportDTO> subjectReports;
        
        if (existingReport != null && existingReport.getSubjectReports() != null && !existingReport.getSubjectReports().isEmpty() && subjectId != null) {
            // For subject-specific reports, use existing subject reports with comments
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
                        subjectDto.setCourseworkGrade(subjectReport.getGrade()); // For backward compatibility, use existing grade as coursework grade
                        subjectDto.setExamGrade(subjectReport.getGrade()); // For backward compatibility, use existing grade as exam grade
                        subjectDto.setComment(subjectReport.getTeacherComment()); // Include teacher comment
                        subjectDto.setTeacherId(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getId() : null);
                        subjectDto.setTeacherName(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getFirstName() + " " + subjectReport.getTeacher().getLastName() : null);
                        return subjectDto;
                    })
                    .collect(Collectors.toList());
        } else {
            // Generate subject reports from StudentSubject relationships
            System.out.println("Generating subject reports from StudentSubject relationships for student: " + student.getId());
            List<StudentSubject> studentSubjects = studentSubjectRepository.findByStudentId(student.getId());
            
            // For class reports, we need to merge existing subject reports with generated ones
            final Map<Long, SubjectReportDTO> existingSubjectReports;
            if (existingReport != null && existingReport.getSubjectReports() != null && !existingReport.getSubjectReports().isEmpty()) {
                existingSubjectReports = existingReport.getSubjectReports().stream()
                    .collect(Collectors.toMap(
                        sr -> sr.getSubject().getId(),
                        sr -> {
                                                    SubjectReportDTO existingDto = new SubjectReportDTO();
                        existingDto.setId(sr.getId());
                        existingDto.setSubjectId(sr.getSubject().getId());
                        existingDto.setSubjectName(sr.getSubject().getName());
                        existingDto.setSubjectCode(sr.getSubject().getCode());
                        existingDto.setCourseworkMark(sr.getCourseworkMark());
                        existingDto.setExamMark(sr.getExamMark());
                        existingDto.setFinalMark(sr.getTotalMark());
                        existingDto.setCourseworkGrade(sr.getGrade()); // For backward compatibility, use existing grade as coursework grade
                        existingDto.setExamGrade(sr.getGrade()); // For backward compatibility, use existing grade as exam grade
                        existingDto.setComment(sr.getTeacherComment());
                        existingDto.setTeacherId(sr.getTeacher() != null ? sr.getTeacher().getId() : null);
                        existingDto.setTeacherName(sr.getTeacher() != null ? sr.getTeacher().getFirstName() + " " + sr.getTeacher().getLastName() : null);
                            return existingDto;
                        }
                    ));
            } else {
                existingSubjectReports = new HashMap<>();
            }
            
            subjectReports = studentSubjects.stream()
                    .filter(studentSubject -> subjectId == null || studentSubject.getSubject().getId().equals(subjectId)) // Process all subjects for class reports, or specific subject for subject reports
                    .map(studentSubject -> {
                        Subject subject = studentSubject.getSubject();
                        System.out.println("Processing subject: " + subject.getName() + " (ID: " + subject.getId() + ")");
                        
                        // Check if we have an existing subject report for this subject
                        if (existingSubjectReports.containsKey(subject.getId())) {
                            System.out.println("Using existing subject report for " + subject.getName());
                            return existingSubjectReports.get(subject.getId());
                        }
                        
                        // Generate new subject report
                        SubjectReportDTO subjectDto = new SubjectReportDTO();
                        subjectDto.setSubjectId(subject.getId());
                        subjectDto.setSubjectName(subject.getName());
                        subjectDto.setSubjectCode(subject.getCode());
                        
                        // Calculate marks from assessments
                        try {
                            System.out.println("Calculating marks for student: " + student.getId() + " (" + student.getFirstName() + " " + student.getLastName() + "), subject: " + subject.getId() + " (" + subject.getName() + "), term: " + term + ", year: " + year);
                            List<Assessment> assessments = assessmentRepository.findByStudentSubjectTermAndYear(
                                    student.getId(), subject.getId(), term, year);
                            System.out.println("Found " + assessments.size() + " assessments for " + subject.getName());
                            if (assessments.size() > 0) {
                                for (Assessment assessment : assessments) {
                                    System.out.println("  - Assessment: " + assessment.getTitle() + ", Score: " + assessment.getScore() + "/" + assessment.getMaxScore() + ", Type: " + assessment.getType());
                                }
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
                            
                            // Calculate total mark
                            double totalMark = 0;
                            if (subjectDto.getCourseworkMark() != null && subjectDto.getExamMark() != null) {
                                totalMark = (subjectDto.getCourseworkMark() * 0.4) + (subjectDto.getExamMark() * 0.6);
                            } else if (subjectDto.getCourseworkMark() != null) {
                                totalMark = subjectDto.getCourseworkMark();
                            } else if (subjectDto.getExamMark() != null) {
                                totalMark = subjectDto.getExamMark();
                            }
                            
                            subjectDto.setFinalMark(totalMark);
                            
                            // Calculate and set separate grades for coursework and exam
                            if (subjectDto.getCourseworkMark() != null && subjectDto.getCourseworkMark() > 0) {
                                subjectDto.setCourseworkGrade(calculateGrade(subjectDto.getCourseworkMark()));
                            }
                            if (subjectDto.getExamMark() != null && subjectDto.getExamMark() > 0) {
                                subjectDto.setExamGrade(calculateGrade(subjectDto.getExamMark()));
                            }
                            
                            System.out.println("Calculated marks for " + subject.getName() + ": Coursework=" + subjectDto.getCourseworkMark() + "(" + subjectDto.getCourseworkGrade() + "), Exam=" + subjectDto.getExamMark() + "(" + subjectDto.getExamGrade() + "), Total=" + totalMark);
                            
                        } catch (Exception e) {
                            System.out.println("Error calculating marks for " + subject.getName() + ": " + e.getMessage());
                            // If error calculating marks, set defaults
                            subjectDto.setCourseworkMark(0.0);
                            subjectDto.setExamMark(0.0);
                            subjectDto.setFinalMark(0.0);
                        }
                        
                        return subjectDto;
                    })
                    .collect(Collectors.toList());
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
        List<SubjectReport> subjectReports = report.getSubjectReports();
        if (subjectReports == null) {
            subjectReports = new ArrayList<>();
            report.setSubjectReports(subjectReports);
        }
        
        SubjectReport subjectReport = subjectReports.stream()
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
                        
                        // Calculate total mark
                        double totalMark = 0;
                        if (newSubjectReport.getCourseworkMark() != null && newSubjectReport.getExamMark() != null) {
                            totalMark = (newSubjectReport.getCourseworkMark() * 0.4) + (newSubjectReport.getExamMark() * 0.6);
                        } else if (newSubjectReport.getCourseworkMark() != null) {
                            totalMark = newSubjectReport.getCourseworkMark();
                        } else if (newSubjectReport.getExamMark() != null) {
                            totalMark = newSubjectReport.getExamMark();
                        }
                        
                        newSubjectReport.setTotalMark(totalMark);
                        
                        // Set grades based on coursework and exam marks
                        if (courseworkTotal > 0) {
                            double courseworkAvg = courseworkTotal / courseworkCount;
                            if (courseworkAvg >= 75) newSubjectReport.setGrade("A");
                            else if (courseworkAvg >= 60) newSubjectReport.setGrade("B");
                            else if (courseworkAvg >= 50) newSubjectReport.setGrade("C");
                            else if (courseworkAvg >= 40) newSubjectReport.setGrade("D");
                            else if (courseworkAvg >= 30) newSubjectReport.setGrade("E");
                            else newSubjectReport.setGrade("U");
                        }
                        if (examTotal > 0) {
                            double examAvg = examTotal / examCount;
                            if (examAvg >= 75) newSubjectReport.setGrade("A");
                            else if (examAvg >= 60) newSubjectReport.setGrade("B");
                            else if (examAvg >= 50) newSubjectReport.setGrade("C");
                            else if (examAvg >= 40) newSubjectReport.setGrade("D");
                            else if (examAvg >= 30) newSubjectReport.setGrade("E");
                            else newSubjectReport.setGrade("U");
                        }
                        
                    } catch (Exception e) {
                        // If error calculating marks, set defaults
                        newSubjectReport.setCourseworkMark(0.0);
                        newSubjectReport.setExamMark(0.0);
                        newSubjectReport.setTotalMark(0.0);
                        newSubjectReport.setGrade("N/A");
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

    /**
     * Update report with attendance statistics for a term
     */
    public void updateReportAttendance(Long reportId, String term, String academicYear) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));
        
        AttendanceStatistics stats = attendanceService.calculateTermAttendance(
            report.getStudent().getId(), term, academicYear);
        
        report.setAttendanceDays(stats.getPresentDays());
        report.setTotalSchoolDays(stats.getTotalSchoolDays());
        
        reportRepository.save(report);
    }

    /**
     * Update all reports for a class with attendance statistics
     */
    public void updateClassReportsAttendance(String form, String section, String term, String academicYear) {
        List<Report> reports = reportRepository.findByStudentFormAndStudentSectionAndTermAndAcademicYear(
            form, section, term, academicYear);
        
        Map<Long, AttendanceStatistics> attendanceStats = attendanceService.getClassTermAttendance(
            form, section, term, academicYear);
        
        for (Report report : reports) {
            AttendanceStatistics stats = attendanceStats.get(report.getStudent().getId());
            if (stats != null) {
                report.setAttendanceDays(stats.getPresentDays());
                report.setTotalSchoolDays(stats.getTotalSchoolDays());
            }
        }
        
        reportRepository.saveAll(reports);
    }

    /**
     * Get all reports for a specific student
     */
    public List<StudentReportDTO> getStudentReports(Long studentId, String username) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        // Get all reports for this student
        List<Report> reports = reportRepository.findByStudentId(studentId);
        
        return reports.stream()
                .map(report -> {
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
                    
                    // Convert subject reports
                    List<SubjectReportDTO> subjectReportDTOs = new ArrayList<>();
                    if (report.getSubjectReports() != null) {
                        for (SubjectReport subjectReport : report.getSubjectReports()) {
                            SubjectReportDTO subjectReportDTO = new SubjectReportDTO();
                            subjectReportDTO.setId(subjectReport.getId());
                            subjectReportDTO.setSubjectId(subjectReport.getSubject().getId());
                            subjectReportDTO.setSubjectName(subjectReport.getSubject().getName());
                            subjectReportDTO.setSubjectCode(subjectReport.getSubject().getCode());
                            subjectReportDTO.setCourseworkMark(subjectReport.getCourseworkMark());
                            subjectReportDTO.setExamMark(subjectReport.getExamMark());
                            subjectReportDTO.setFinalMark(subjectReport.getTotalMark());
                            subjectReportDTO.setCourseworkGrade(subjectReport.getGrade()); // For backward compatibility
                            subjectReportDTO.setExamGrade(subjectReport.getGrade()); // For backward compatibility
                            subjectReportDTO.setComment(subjectReport.getTeacherComment());
                            subjectReportDTO.setTeacherId(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getId() : null);
                            subjectReportDTO.setTeacherName(subjectReport.getTeacher() != null ? 
                                subjectReport.getTeacher().getFirstName() + " " + subjectReport.getTeacher().getLastName() : null);
                            subjectReportDTOs.add(subjectReportDTO);
                        }
                    }
                    dto.setSubjectReports(subjectReportDTOs);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a specific report by ID
     */
    public StudentReportDTO getReportById(Long reportId, String username) {
        Report report = reportRepository.findById(reportId)
                .orElse(null);
        
        if (report == null) {
            return null;
        }
        
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
        
        // Convert subject reports
        List<SubjectReportDTO> subjectReportDTOs = new ArrayList<>();
        if (report.getSubjectReports() != null) {
            for (SubjectReport subjectReport : report.getSubjectReports()) {
                SubjectReportDTO subjectReportDTO = new SubjectReportDTO();
                subjectReportDTO.setId(subjectReport.getId());
                subjectReportDTO.setSubjectId(subjectReport.getSubject().getId());
                subjectReportDTO.setSubjectName(subjectReport.getSubject().getName());
                subjectReportDTO.setSubjectCode(subjectReport.getSubject().getCode());
                subjectReportDTO.setCourseworkMark(subjectReport.getCourseworkMark());
                subjectReportDTO.setExamMark(subjectReport.getExamMark());
                subjectReportDTO.setFinalMark(subjectReport.getTotalMark());
                subjectReportDTO.setCourseworkGrade(subjectReport.getGrade()); // For backward compatibility
                subjectReportDTO.setExamGrade(subjectReport.getGrade()); // For backward compatibility
                subjectReportDTO.setComment(subjectReport.getTeacherComment());
                subjectReportDTO.setTeacherId(subjectReport.getTeacher() != null ? subjectReport.getTeacher().getId() : null);
                subjectReportDTO.setTeacherName(subjectReport.getTeacher() != null ? 
                    subjectReport.getTeacher().getFirstName() + " " + subjectReport.getTeacher().getLastName() : null);
                subjectReportDTOs.add(subjectReportDTO);
            }
        }
        dto.setSubjectReports(subjectReportDTOs);
        
        return dto;
    }
}