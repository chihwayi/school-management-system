package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.entity.*;
import com.devtech.school_management_system.repository.*;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        // Get all students in the class
        List<Student> students = studentRepository.findByFormAndSectionAndYear(form, section, year);
        
        return students.stream()
                .map(student -> createStudentReportDTO(student, term, year))
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
        
        // Get student's subjects from StudentSubject relationships
        List<StudentSubject> studentSubjects = studentSubjectRepository.findByStudentId(student.getId());
        List<SubjectReportDTO> subjectReports = studentSubjects.stream()
                .map(studentSubject -> {
                    Subject subject = studentSubject.getSubject();
                    SubjectReportDTO subjectDto = new SubjectReportDTO();
                    subjectDto.setSubjectId(subject.getId());
                    subjectDto.setSubjectName(subject.getName());
                    subjectDto.setSubjectCode(subject.getCode());
                    
                    // Get assessments for this student and subject
                    try {
                        List<Assessment> assessments = assessmentRepository.findByStudentSubjectTermAndYear(
                                student.getId(), subject.getId(), term, year);
                        
                        // Calculate marks
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
                        
                        if (courseworkCount > 0) subjectDto.setCourseworkMark(courseworkTotal / courseworkCount);
                        if (examCount > 0) subjectDto.setExamMark(examTotal / examCount);
                        
                        // Calculate final mark
                        double finalMark = 0;
                        if (subjectDto.getCourseworkMark() != null && subjectDto.getExamMark() != null) {
                            finalMark = (subjectDto.getCourseworkMark() * 0.4) + (subjectDto.getExamMark() * 0.6);
                        } else if (subjectDto.getCourseworkMark() != null) {
                            finalMark = subjectDto.getCourseworkMark();
                        } else if (subjectDto.getExamMark() != null) {
                            finalMark = subjectDto.getExamMark();
                        }
                        
                        subjectDto.setFinalMark(finalMark);
                    } catch (Exception e) {
                        // If there's an error, just set final mark to 0
                        subjectDto.setFinalMark(0.0);
                    }
                    
                    return subjectDto;
                })
                .collect(Collectors.toList());
        
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
                        
                        // Set grade based on total mark
                        if (totalMark >= 75) newSubjectReport.setGrade("A");
                        else if (totalMark >= 60) newSubjectReport.setGrade("B");
                        else if (totalMark >= 50) newSubjectReport.setGrade("C");
                        else if (totalMark >= 40) newSubjectReport.setGrade("D");
                        else if (totalMark >= 30) newSubjectReport.setGrade("E");
                        else newSubjectReport.setGrade("U");
                        
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
}