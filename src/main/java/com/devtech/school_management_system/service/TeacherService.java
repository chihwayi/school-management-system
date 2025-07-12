package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.TeacherSubjectClass;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.ClassGroupRepository;
import com.devtech.school_management_system.repository.TeacherRepository;
import com.devtech.school_management_system.repository.TeacherSubjectClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherSubjectClassRepository teacherSubjectClassRepository;
    private final ClassGroupRepository classGroupRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository,
                          TeacherSubjectClassRepository teacherSubjectClassRepository,
                          ClassGroupRepository classGroupRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
        this.classGroupRepository = classGroupRepository;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Teacher getTeacherByUsername(String username) {
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with username: " + username));
        // Initialize lazy-loaded user to avoid serialization issues
        if (teacher.getUser() != null) {
            teacher.getUser().getUsername(); // Force initialization
        }
        return teacher;
    }

    public Teacher createTeacher(Teacher teacher) {
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = getTeacherById(id);

        teacher.setFirstName(teacherDetails.getFirstName());
        teacher.setLastName(teacherDetails.getLastName());
        teacher.setEmployeeId(teacherDetails.getEmployeeId());
        teacher.setUpdatedAt(LocalDateTime.now());

        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    public List<TeacherSubjectClass> getAssignedSubjectsAndClasses(String username) {
        Teacher teacher = getTeacherByUsername(username);
        return teacherSubjectClassRepository.findByTeacherId(teacher.getId());
    }

    public List<ClassGroup> getSupervisedClasses(String username) {
        Teacher teacher = getTeacherByUsername(username);
        return classGroupRepository.findByClassTeacherId(teacher.getId());
    }

    public boolean canTeacherRecordForStudentSubject(Long teacherId, Long studentSubjectId) {
        // Implementation logic to check if teacher can record for student subject
        return teacherSubjectClassRepository.existsByTeacherIdAndStudentSubjectId(teacherId, studentSubjectId);
    }

    public boolean canTeacherCommentOnSubject(Long teacherId, Long reportId, Long subjectId) {
        // Implementation logic to check if teacher can comment on subject
        return teacherSubjectClassRepository.existsByTeacherIdAndSubjectId(teacherId, subjectId);
    }
}
