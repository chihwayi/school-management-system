package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.ClassGroupWithStudentsDTO;
import com.devtech.school_management_system.dto.TeacherSubjectClassDTO;
import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.TeacherSubjectClass;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.ClassGroupRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.TeacherRepository;
import com.devtech.school_management_system.repository.TeacherSubjectClassRepository;
import org.hibernate.Hibernate;
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
    private final StudentRepository studentRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository,
                          TeacherSubjectClassRepository teacherSubjectClassRepository,
                          ClassGroupRepository classGroupRepository,
                          StudentRepository studentRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
        this.classGroupRepository = classGroupRepository;
        this.studentRepository = studentRepository;
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

    public List<TeacherSubjectClassDTO> getAssignedSubjectsAndClassesDTO(String username) {
        Teacher teacher = getTeacherByUsername(username);
        List<TeacherSubjectClass> assignments = teacherSubjectClassRepository.findByTeacherId(teacher.getId());
        return assignments.stream()
                .map(tsc -> new TeacherSubjectClassDTO(
                        tsc.getId(),
                        tsc.getTeacher().getId(),
                        tsc.getTeacher().getFullName(),
                        tsc.getSubject().getId(),
                        tsc.getSubject().getName(),
                        tsc.getSubject().getCode(),
                        tsc.getForm(),
                        tsc.getSection(),
                        tsc.getAcademicYear()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Teacher> getAllTeachersWithUserDetails() {
        List<Teacher> teachers = teacherRepository.findAll();
        for (Teacher teacher : teachers) {
            if (teacher.getUser() != null) {
                Hibernate.initialize(teacher.getUser());
                Hibernate.initialize(teacher.getUser().getRoles());
            }
        }
        return teachers;
    }



    public List<ClassGroupWithStudentsDTO> getSupervisedClasses(String username) {
        Teacher teacher = getTeacherByUsername(username);
        List<ClassGroup> classGroups = classGroupRepository.findByClassTeacherId(teacher.getId());
        
        return classGroups.stream().map(classGroup -> {
            ClassGroupWithStudentsDTO dto = new ClassGroupWithStudentsDTO();
            dto.setId(classGroup.getId());
            dto.setForm(classGroup.getForm());
            dto.setSection(classGroup.getSection());
            dto.setAcademicYear(classGroup.getAcademicYear());
            dto.setLevel(classGroup.getLevel());
            dto.setClassCapacity(classGroup.getClassCapacity());
            dto.setCreatedAt(classGroup.getCreatedAt());
            dto.setUpdatedAt(classGroup.getUpdatedAt());
            
            if (classGroup.getClassTeacher() != null) {
                dto.setClassTeacherId(classGroup.getClassTeacher().getId());
                dto.setClassTeacherName(classGroup.getClassTeacher().getFullName());
            }
            
            // Get student count for this class
            List<Student> students = studentRepository.findByFormAndSectionAndYear(
                classGroup.getForm(), 
                classGroup.getSection(), 
                classGroup.getAcademicYear()
            );
            dto.setStudentCount(students.size());
            
            return dto;
        }).collect(java.util.stream.Collectors.toList());
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
