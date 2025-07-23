package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Report;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.entity.SubjectReport;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.repository.ClassGroupRepository;
import com.devtech.school_management_system.repository.ReportRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.SubjectRepository;
import com.devtech.school_management_system.repository.SubjectReportRepository;
import com.devtech.school_management_system.repository.TeacherRepository;
import com.devtech.school_management_system.repository.StudentSubjectRepository;
import com.devtech.school_management_system.entity.StudentSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final SubjectReportRepository subjectReportRepository;
    private final StudentRepository studentRepository;
    private final ClassGroupRepository classGroupRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository,
                         SubjectReportRepository subjectReportRepository,
                         StudentRepository studentRepository,
                         ClassGroupRepository classGroupRepository,
                         SubjectRepository subjectRepository,
                         TeacherRepository teacherRepository,
                         StudentSubjectRepository studentSubjectRepository) {
        this.reportRepository = reportRepository;
        this.subjectReportRepository = subjectReportRepository;
        this.studentRepository = studentRepository;
        this.classGroupRepository = classGroupRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    public List<Report> generateClassReports(Long classGroupId, String term, String year) {
        ClassGroup classGroup = classGroupRepository.findById(classGroupId)
                .orElseThrow(() -> new RuntimeException("Class group not found"));

        List<Student> students = studentRepository.findByFormAndSectionAndYear(
                classGroup.getForm(), classGroup.getSection(), classGroup.getAcademicYear());

        List<Report> reports = new ArrayList<>();
        for (Student student : students) {
            // Check if report already exists
            Optional<Report> existingReport = reportRepository.findByStudentIdAndTermAndAcademicYear(
                    student.getId(), term, year);
            
            if (existingReport.isPresent()) {
                reports.add(existingReport.get());
                continue;
            }

            // Create new report
            Report report = new Report();
            report.setStudent(student);
            report.setTerm(term);
            report.setAcademicYear(year);
            report.setClassTeacher(classGroup.getClassTeacher());
            report.setFinalized(false);
            
            Report savedReport = reportRepository.save(report);
            
            // Create subject reports for each subject the student is enrolled in
            List<StudentSubject> studentSubjects = studentSubjectRepository.findByStudentId(student.getId());
            for (StudentSubject studentSubject : studentSubjects) {
                Subject subject = studentSubject.getSubject();
                SubjectReport subjectReport = new SubjectReport();
                subjectReport.setReport(savedReport);
                subjectReport.setSubject(subject);
                subjectReportRepository.save(subjectReport);
            }
            
            reports.add(savedReport);
        }
        
        return reports;
    }

    public List<Report> getStudentReports(Long studentId) {
        return reportRepository.findByStudentId(studentId);
    }

    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public SubjectReport addSubjectComment(Long reportId, Long subjectId, String comment, Long teacherId) {
        SubjectReport subjectReport = subjectReportRepository.findByReportIdAndSubjectId(reportId, subjectId)
                .orElseThrow(() -> new RuntimeException("Subject report not found"));
        
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        subjectReport.setTeacherComment(comment);
        subjectReport.setTeacher(teacher);
        
        return subjectReportRepository.save(subjectReport);
    }

    public Report addOverallComment(Long reportId, String comment, Long teacherId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setOverallComment(comment);
        
        return reportRepository.save(report);
    }

    public Report finalizeReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setFinalized(true);
        
        return reportRepository.save(report);
    }
    
    public Report addPrincipalComment(Long reportId, String comment) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setPrincipalComment(comment);
        
        return reportRepository.save(report);
    }
    
    public Report updateAttendance(Long reportId, Integer attendanceDays, Integer totalSchoolDays) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setAttendanceDays(attendanceDays);
        report.setTotalSchoolDays(totalSchoolDays);
        
        return reportRepository.save(report);
    }
    
    public List<Report> getClassReports(Long classGroupId, String term, String year) {
        ClassGroup classGroup = classGroupRepository.findById(classGroupId)
                .orElseThrow(() -> new RuntimeException("Class group not found"));
        
        return reportRepository.findByFormAndSectionAndTermAndAcademicYear(
                classGroup.getForm(), classGroup.getSection(), term, year);
    }
    
    public Report updatePaymentStatus(Long reportId, String paymentStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setPaymentStatus(paymentStatus);
        
        return reportRepository.save(report);
    }
    
    public SubjectReport addTeacherSignature(Long reportId, Long subjectId, String signatureUrl) {
        SubjectReport subjectReport = subjectReportRepository.findByReportIdAndSubjectId(reportId, subjectId)
                .orElseThrow(() -> new RuntimeException("Subject report not found"));
        
        subjectReport.setTeacherSignatureUrl(signatureUrl);
        
        return subjectReportRepository.save(subjectReport);
    }
    
    public Report addClassTeacherSignature(Long reportId, String signatureUrl) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setClassTeacherSignatureUrl(signatureUrl);
        
        return reportRepository.save(report);
    }
    
    public boolean isClassTeacherForReport(Long teacherId, Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        return report.getClassTeacher() != null && 
               report.getClassTeacher().getId().equals(teacherId);
    }
}