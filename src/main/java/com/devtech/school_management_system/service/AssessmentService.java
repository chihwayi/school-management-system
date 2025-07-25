package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Assessment;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.entity.StudentSubject;
import com.devtech.school_management_system.dto.AssessmentResponseDTO;
import com.devtech.school_management_system.enums.AssessmentType;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.AssessmentRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.SubjectRepository;
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
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public AssessmentService(AssessmentRepository assessmentRepository,
                             StudentSubjectRepository studentSubjectRepository,
                             TeacherSubjectClassRepository teacherSubjectClassRepository,
                             StudentRepository studentRepository,
                             SubjectRepository subjectRepository) {
        this.assessmentRepository = assessmentRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
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

    public AssessmentResponseDTO recordAssessmentByStudentAndSubject(Long studentId, Long subjectId, String title, LocalDate date,
                                                          Double score, Double maxScore, AssessmentType type,
                                                          String term, String academicYear) {
        // Find or create StudentSubject relationship
        StudentSubject studentSubject = studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseGet(() -> {
                    // Create new StudentSubject relationship if it doesn't exist
                    Student student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));
                    
                    StudentSubject newStudentSubject = new StudentSubject();
                    newStudentSubject.setStudent(student);
                    newStudentSubject.setSubject(subject);
                    newStudentSubject.setAssignedDate(java.time.LocalDateTime.now());
                    return studentSubjectRepository.save(newStudentSubject);
                });

        Assessment assessment = new Assessment();
        assessment.setStudentSubject(studentSubject);
        assessment.setTitle(title);
        assessment.setDate(date);
        assessment.setScore(score);
        assessment.setMaxScore(maxScore);
        assessment.setType(type);
        assessment.setTerm(term);
        assessment.setAcademicYear(academicYear);

        Assessment savedAssessment = assessmentRepository.save(assessment);
        return convertToDTO(savedAssessment);
    }

    public List<AssessmentResponseDTO> getStudentSubjectAssessments(Long studentId, Long subjectId) {
        List<Assessment> assessments = assessmentRepository.findByStudentIdAndSubjectId(studentId, subjectId);
        return assessments.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<AssessmentResponseDTO> getStudentTermAssessments(Long studentId, String term, String year) {
        List<Assessment> assessments = assessmentRepository.findByStudentIdAndTermAndAcademicYear(studentId, term, year);
        return assessments.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public AssessmentResponseDTO updateAssessment(Long id, String title, LocalDate date, Double score, Double maxScore) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));

        assessment.setTitle(title);
        assessment.setDate(date);
        assessment.setScore(score);
        assessment.setMaxScore(maxScore);

        Assessment updatedAssessment = assessmentRepository.save(assessment);
        return convertToDTO(updatedAssessment);
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
    
    private AssessmentResponseDTO convertToDTO(Assessment assessment) {
        StudentSubject studentSubject = assessment.getStudentSubject();
        return new AssessmentResponseDTO(
                assessment.getId(),
                assessment.getTitle(),
                assessment.getDate(),
                assessment.getScore(),
                assessment.getMaxScore(),
                assessment.getType(),
                assessment.getTerm(),
                assessment.getAcademicYear(),
                studentSubject.getStudent().getId(),
                studentSubject.getStudent().getFirstName(),
                studentSubject.getStudent().getLastName(),
                studentSubject.getStudent().getForm(),
                studentSubject.getStudent().getSection(),
                studentSubject.getSubject().getId(),
                studentSubject.getSubject().getName(),
                studentSubject.getSubject().getCode()
        );
    }
}