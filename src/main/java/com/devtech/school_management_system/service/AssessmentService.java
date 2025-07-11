package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Assessment;
import com.devtech.school_management_system.entity.StudentSubject;
import com.devtech.school_management_system.enums.AssessmentType;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.AssessmentRepository;
import com.devtech.school_management_system.repository.StudentSubjectRepository;
import com.devtech.school_management_system.repository.TeacherSubjectClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final TeacherSubjectClassRepository teacherSubjectClassRepository;

    @Autowired
    public AssessmentService(AssessmentRepository assessmentRepository,
                             StudentSubjectRepository studentSubjectRepository,
                             TeacherSubjectClassRepository teacherSubjectClassRepository) {
        this.assessmentRepository = assessmentRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
    }

    public Assessment recordAssessment(Long studentSubjectId, String title, LocalDate date,
                                       Double score, Double maxScore, AssessmentType type,
                                       String term, String academicYear) {
        StudentSubject studentSubject = studentSubjectRepository.findById(studentSubjectId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentSubject not found with id: " + studentSubjectId));

        Assessment assessment = new Assessment();
        assessment.setStudentSubject(studentSubject);
        assessment.setTitle(title);
        assessment.setDate(date);
        assessment.setScore(score);
        assessment.setMaxScore(maxScore);
        assessment.setType(type);
        assessment.setTerm(term);
        assessment.setAcademicYear(academicYear);

        return assessmentRepository.save(assessment);
    }

    public List<Assessment> getStudentSubjectAssessments(Long studentId, Long subjectId) {
        return assessmentRepository.findByStudentSubjectStudentIdAndStudentSubjectSubjectId(studentId, subjectId);
    }

    public List<Assessment> getStudentTermAssessments(Long studentId, String term, String year) {
        return assessmentRepository.findByStudentSubjectStudentIdAndTermAndAcademicYear(studentId, term, year);
    }

    public Assessment updateAssessment(Long id, String title, LocalDate date, Double score, Double maxScore) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));

        assessment.setTitle(title);
        assessment.setDate(date);
        assessment.setScore(score);
        assessment.setMaxScore(maxScore);

        return assessmentRepository.save(assessment);
    }

    public Assessment getAssessmentById(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
    }

    public void deleteAssessment(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
        assessmentRepository.delete(assessment);
    }

    public boolean canTeacherUpdateAssessment(Long teacherId, Long assessmentId) {
        Assessment assessment = getAssessmentById(assessmentId);
        StudentSubject studentSubject = assessment.getStudentSubject();

        // Check if teacher is assigned to this subject and class
        return teacherSubjectClassRepository.existsByEmployeeIdAndSubjectIdAndFormAndSection(
                teacherId,
                studentSubject.getSubject().getId(),
                studentSubject.getStudent().getForm(),
                studentSubject.getStudent().getSection()
        );
    }

    public List<Assessment> getAssessmentsByStudentSubject(Long studentSubjectId) {
        return assessmentRepository.findByStudentSubjectId(studentSubjectId);
    }

    public Double calculateCourseworkAverage(Long studentSubjectId, String term, String academicYear) {
        List<Assessment> courseworkAssessments = assessmentRepository
                .findByStudentSubjectIdAndTypeAndTermAndAcademicYear(
                        studentSubjectId, AssessmentType.COURSEWORK, term, academicYear);

        if (courseworkAssessments.isEmpty()) {
            return 0.0;
        }

        return courseworkAssessments.stream()
                .mapToDouble(assessment -> (assessment.getScore() / assessment.getMaxScore()) * 100)
                .average()
                .orElse(0.0);
    }

    public Double getFinalExamScore(Long studentSubjectId, String term, String academicYear) {
        List<Assessment> finalExams = assessmentRepository
                .findByStudentSubjectIdAndTypeAndTermAndAcademicYear(
                        studentSubjectId, AssessmentType.FINAL_EXAM, term, academicYear);

        if (finalExams.isEmpty()) {
            return null;
        }

        // Return the latest final exam score
        Assessment latestExam = finalExams.stream()
                .max((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
                .orElse(null);

        return latestExam.getScore() / latestExam.getMaxScore() * 100;
    }
}