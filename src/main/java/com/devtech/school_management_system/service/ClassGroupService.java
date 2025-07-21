package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.ClassGroupDTO;
import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.ClassGroupRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ClassGroupService {

    private final ClassGroupRepository classGroupRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public ClassGroupService(ClassGroupRepository classGroupRepository,
                             StudentRepository studentRepository,
                             TeacherRepository teacherRepository) {
        this.classGroupRepository = classGroupRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    public List<ClassGroup> getAllClassGroups() {
        return classGroupRepository.findAllWithClassTeachers();
    }
    
    public List<ClassGroupDTO> getAllClassGroupsDTO() {
        List<ClassGroup> classGroups = classGroupRepository.findAllWithClassTeachers();
        return classGroups.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public ClassGroupDTO convertToDTO(ClassGroup classGroup) {
        ClassGroupDTO dto = new ClassGroupDTO();
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
    }

    public ClassGroup getClassGroupById(Long id) {
        return classGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup not found with id: " + id));
    }

    public ClassGroup createClassGroup(ClassGroup classGroup) {
        // Check if class group already exists
        Optional<ClassGroup> existingClass = classGroupRepository
                .findByFormAndSectionAndAcademicYear(
                        classGroup.getForm(),
                        classGroup.getSection(),
                        classGroup.getAcademicYear()
                );

        if (existingClass.isPresent()) {
            throw new IllegalArgumentException("Class group already exists for " +
                    classGroup.getForm() + " " + classGroup.getSection() + " " + classGroup.getAcademicYear());
        }

        return classGroupRepository.save(classGroup);
    }

    public ClassGroup updateClassGroup(Long id, ClassGroup classGroupDetails) {
        ClassGroup classGroup = getClassGroupById(id);

        classGroup.setForm(classGroupDetails.getForm());
        classGroup.setSection(classGroupDetails.getSection());
        classGroup.setAcademicYear(classGroupDetails.getAcademicYear());
        classGroup.setLevel(classGroupDetails.getLevel());
        classGroup.setClassCapacity(classGroupDetails.getClassCapacity());

        return classGroupRepository.save(classGroup);
    }

    public void deleteClassGroup(Long id) {
        ClassGroup classGroup = getClassGroupById(id);

        // Check if there are students in this class
        List<Student> students = studentRepository.findByFormAndSection(classGroup.getForm(), classGroup.getSection());
        if (!students.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete class group with enrolled students");
        }

        classGroupRepository.delete(classGroup);
    }

    public ClassGroup getClassGroupByDetails(String form, String section, String year) {
        return classGroupRepository.findByFormAndSectionAndAcademicYear(form, section, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ClassGroup not found for " + form + " " + section + " " + year));
    }

    public List<Student> getStudentsInClass(Long classGroupId) {
        ClassGroup classGroup = getClassGroupById(classGroupId);
        return studentRepository.findByFormAndSectionAndYear(
            classGroup.getForm(), 
            classGroup.getSection(), 
            classGroup.getAcademicYear()
        );
    }

    @Autowired
    private UserService userService;
    
    public ClassGroup assignClassTeacher(Long classGroupId, Long teacherId) {
        ClassGroup classGroup = getClassGroupById(classGroupId);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        // Assign the teacher to the class
        classGroup.setClassTeacher(teacher);
        
        // Add CLASS_TEACHER role to the teacher's user
        if (teacher.getUser() != null) {
            Set<String> roles = teacher.getUser().getRoles().stream()
                .map(role -> role.getName().name().replace("ROLE_", ""))
                .collect(java.util.stream.Collectors.toSet());
            
            // Add CLASS_TEACHER role if not already present
            if (!roles.contains("CLASS_TEACHER")) {
                roles.add("CLASS_TEACHER");
                userService.updateRoles(teacher.getUser().getUsername(), roles);
            }
        }
        
        return classGroupRepository.save(classGroup);
    }

    public List<ClassGroup> getClassGroupsByTeacher(Long teacherId) {
        return classGroupRepository.findByClassTeacherId(teacherId);
    }

    public List<ClassGroup> getClassGroupsByLevel(String level) {
        return classGroupRepository.findByLevel(level);
    }

    public List<ClassGroup> getClassGroupsByAcademicYear(String academicYear) {
        return classGroupRepository.findByAcademicYear(academicYear);
    }

    public ClassGroup removeClassTeacher(Long classGroupId) {
        ClassGroup classGroup = getClassGroupById(classGroupId);
        classGroup.setClassTeacher(null);
        return classGroupRepository.save(classGroup);
    }

    public boolean isClassAtCapacity(Long classGroupId) {
        ClassGroup classGroup = getClassGroupById(classGroupId);
        List<Student> students = getStudentsInClass(classGroupId);

        return classGroup.getClassCapacity() != null &&
                students.size() >= classGroup.getClassCapacity();
    }

    public int getClassSize(Long classGroupId) {
        return getStudentsInClass(classGroupId).size();
    }

    public List<ClassGroup> getAvailableClassGroups() {
        return classGroupRepository.findAll().stream()
                .filter(classGroup -> !isClassAtCapacity(classGroup.getId()))
                .toList();
    }

}