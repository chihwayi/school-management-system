package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.TeacherSubjectClass;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.SubjectRepository;
import com.devtech.school_management_system.repository.TeacherRepository;
import com.devtech.school_management_system.repository.TeacherSubjectClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TeacherAssignmentService {

    private final TeacherSubjectClassRepository teacherSubjectClassRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public TeacherAssignmentService(TeacherSubjectClassRepository teacherSubjectClassRepository,
                                    TeacherRepository teacherRepository,
                                    SubjectRepository subjectRepository) {
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    public TeacherSubjectClass assignTeacherToSubjectAndClass(Long teacherId, Long subjectId,
                                                              String form, String section, String academicYear) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        TeacherSubjectClass assignment = new TeacherSubjectClass();
        assignment.setTeacher(teacher);
        assignment.setSubject(subject);
        assignment.setForm(form);
        assignment.setSection(section);
        assignment.setAcademicYear(academicYear);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        return teacherSubjectClassRepository.save(assignment);
    }

    public void removeTeacherAssignment(Long id) {
        if (!teacherSubjectClassRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher assignment not found with id: " + id);
        }
        teacherSubjectClassRepository.deleteById(id);
    }

    public List<TeacherSubjectClass> getTeacherAssignments(Long teacherId) {
        return teacherSubjectClassRepository.findByTeacherId(teacherId);
    }

    public TeacherSubjectClass getAssignmentForClass(Long subjectId, String form, String section, String year) {
        return teacherSubjectClassRepository.findBySubjectIdAndFormAndSectionAndAcademicYear(
                        subjectId, form, section, year)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found for the specified class"));
    }
}
