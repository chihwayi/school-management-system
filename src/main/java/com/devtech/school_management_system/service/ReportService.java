package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.*;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassGroupRepository classGroupRepository;

    @Autowired
    private SubjectReportRepository subjectReportRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentSubjectRepository studentSubjectRepository;

    public List<Report> generateClassReports(Long classGroupId, String term, String academicYear) {
        ClassGroup classGroup = classGroupRepository.findById(classGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Class group not found with id: " + classGroupId));

        List<Student> students = studentRepository.findByFormAndSection(classGroup.getForm(), classGroup.getSection());

        return students.stream()
                .map(student -> generateStudentReport(student, term, academicYear))
                .collect(Collectors.toList());
    }

    public Report generateStudentReport(Student student, String term, String academicYear) {
        // Check if report already exists
        Report existingReport = reportRepository.findByStudentIdAndTermAndAcademicYear(student.getId(), term, academicYear).orElse(null);
        if (existingReport != null) {
            return existingReport;
        }

        Report report = new Report();
        report.setStudent(student);
        report.setTerm(term);
        report.setAcademicYear(academicYear);
        report.setCreatedAt(LocalDateTime.now());
        report.setFinalized(false);

        report = reportRepository.save(report);

        // Generate subject reports
        List<StudentSubject> studentSubjects = studentSubjectRepository.findByStudentId(student.getId());
        for (StudentSubject studentSubject : studentSubjects) {
            generateSubjectReport(report, studentSubject, term, academicYear);
        }

        return report;
    }

    private void generateSubjectReport(Report report, StudentSubject studentSubject, String term, String academicYear) {
        SubjectReport subjectReport = new SubjectReport();
        subjectReport.setReport(report);
        subjectReport.setSubject(studentSubject.getSubject());

        // Calculate coursework average
        List<Assessment> courseworkAssessments = assessmentRepository
                .findByStudentSubjectIdAndTypeAndTermAndAcademicYear(
                        studentSubject.getId(), com.devtech.school_management_system.enums.AssessmentType.COURSEWORK, term, academicYear);

        if (!courseworkAssessments.isEmpty()) {
            double courseworkAverage = courseworkAssessments.stream()
                    .mapToDouble(Assessment::getPercentage)
                    .average()
                    .orElse(0.0);
            subjectReport.setCourseworkMark(courseworkAverage);
        }

        // Get final exam mark
        Assessment finalExam = assessmentRepository
                .findByStudentSubjectIdAndTypeAndTermAndAcademicYear(
                        studentSubject.getId(), com.devtech.school_management_system.enums.AssessmentType.FINAL_EXAM, term, academicYear)
                .stream()
                .findFirst()
                .orElse(null);

        if (finalExam != null) {
            subjectReport.setExamMark(finalExam.getPercentage());
        }

        // Calculate total mark (you can adjust the weighting as needed)
        double totalMark = (subjectReport.getCourseworkMark() * 0.4) + (subjectReport.getExamMark() * 0.6);
        subjectReport.setTotalMark(totalMark);

        // Calculate grade based on total mark
        subjectReport.setGrade(calculateGrade(totalMark, studentSubject.getStudent().getLevel()));

        subjectReportRepository.save(subjectReport);
    }

    private String calculateGrade(double totalMark, String level) {
        if ("O Level".equals(level)) {
            if (totalMark >= 80) return "A";
            else if (totalMark >= 70) return "B";
            else if (totalMark >= 60) return "C";
            else if (totalMark >= 50) return "D";
            else if (totalMark >= 40) return "E";
            else return "U";
        } else { // A Level
            if (totalMark >= 80) return "A";
            else if (totalMark >= 70) return "B";
            else if (totalMark >= 60) return "C";
            else if (totalMark >= 50) return "D";
            else if (totalMark >= 40) return "E";
            else return "U";
        }
    }

    public List<Report> getStudentReports(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return reportRepository.findByStudentId(student.getId());
    }

    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
    }

    public SubjectReport addSubjectComment(Long reportId, Long subjectId, String comment, Long teacherId) {
        Report report = getReportById(reportId);

        SubjectReport subjectReport = subjectReportRepository.findByReportIdAndSubjectId(report.getId(), subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject report not found"));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        subjectReport.setTeacherComment(comment);
        subjectReport.setTeacher(teacher);
        subjectReport.setUpdatedAt(LocalDateTime.now());

        return subjectReportRepository.save(subjectReport);
    }

    public Report addOverallComment(Long reportId, String comment, Long teacherId) {
        Report report = getReportById(reportId);

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        report.setOverallComment(comment);
        report.setClassTeacher(teacher);
        report.setUpdatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    public Report finalizeReport(Long reportId) {
        Report report = getReportById(reportId);
        report.setFinalized(true);
        return reportRepository.save(report);
    }

    public List<Report> getClassReports(Long classGroupId, String term, String academicYear) {
        ClassGroup classGroup = classGroupRepository.findById(classGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Class group not found with id: " + classGroupId));

        return reportRepository.findByFormAndSectionAndTermAndAcademicYear(classGroup.getForm(), classGroup.getSection(), term, academicYear);
    }

    public boolean isClassTeacherForReport(Long teacherId, Long reportId) {
        Report report = getReportById(reportId);
        // Find the class group for this student
        ClassGroup classGroup = classGroupRepository.findByFormAndSectionAndAcademicYear(
                report.getStudent().getForm(), report.getStudent().getSection(), report.getAcademicYear())
                .orElse(null);
        return classGroup != null && classGroup.getClassTeacher() != null
                && classGroup.getClassTeacher().getId().equals(teacherId);
    }
}
