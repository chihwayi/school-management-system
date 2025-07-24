package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.TeacherSubjectClassDTO;
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
import java.util.Map;

@Service
@Transactional
public class TeacherAssignmentService {

    private final TeacherSubjectClassRepository teacherSubjectClassRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClassGroupService classGroupService;

    @Autowired
    public TeacherAssignmentService(TeacherSubjectClassRepository teacherSubjectClassRepository,
                                    TeacherRepository teacherRepository,
                                    SubjectRepository subjectRepository,
                                    ClassGroupService classGroupService) {
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.classGroupService = classGroupService;
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
    
    @Transactional
    public List<TeacherSubjectClass> saveBulkAssignments(Long teacherId, List<Map<String, Object>> assignments) {
        System.out.println("Starting bulk assignment for teacher: " + teacherId);
        System.out.println("Number of assignments to process: " + assignments.size());
        
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        System.out.println("Found teacher: " + teacher.getFullName());
        
        // First, remove all existing assignments for this teacher
        teacherSubjectClassRepository.deleteByTeacherId(teacherId);
        System.out.println("Deleted existing assignments for teacher: " + teacherId);
        
        // Then create new assignments
        for (int i = 0; i < assignments.size(); i++) {
            Map<String, Object> assignment = assignments.get(i);
            System.out.println("Processing assignment " + (i + 1) + ": " + assignment);
            
            Long subjectId = Long.valueOf(assignment.get("subjectId").toString());
            Long classGroupId = Long.valueOf(assignment.get("classGroupId").toString());
            
            System.out.println("Subject ID: " + subjectId + ", Class Group ID: " + classGroupId);
            
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));
            
            // Get actual class details from the class group
            var classGroup = classGroupService.getClassGroupById(classGroupId);
            String form = classGroup.getForm();
            String section = classGroup.getSection();
            String academicYear = classGroup.getAcademicYear();
            
            System.out.println("Class details - Form: " + form + ", Section: " + section + ", Year: " + academicYear);
            
            TeacherSubjectClass newAssignment = new TeacherSubjectClass();
            newAssignment.setTeacher(teacher);
            newAssignment.setSubject(subject);
            newAssignment.setForm(form);
            newAssignment.setSection(section);
            newAssignment.setAcademicYear(academicYear);
            newAssignment.setCreatedAt(LocalDateTime.now());
            newAssignment.setUpdatedAt(LocalDateTime.now());
            
            TeacherSubjectClass saved = teacherSubjectClassRepository.save(newAssignment);
            System.out.println("Saved assignment with ID: " + saved.getId());
        }
        
        List<TeacherSubjectClass> result = getTeacherAssignments(teacherId);
        System.out.println("Returning " + result.size() + " assignments for teacher: " + teacherId);
        return result;
    }

    public List<TeacherSubjectClassDTO> getTeacherAssignmentsDTO(Long teacherId) {
        List<TeacherSubjectClass> assignments = teacherSubjectClassRepository.findByTeacherId(teacherId);
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

    public TeacherSubjectClass getAssignmentForClass(Long subjectId, String form, String section, String year) {
        return teacherSubjectClassRepository.findBySubjectIdAndFormAndSectionAndAcademicYear(
                        subjectId, form, section, year)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found for the specified class"));
    }
}
