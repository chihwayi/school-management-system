package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.*;
import com.devtech.school_management_system.dto.TeacherSubjectAssignmentDTO;
import com.devtech.school_management_system.dto.StudentDTO;
import com.devtech.school_management_system.enums.SubjectCategory;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherSubjectClassRepository teacherSubjectClassRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository,
                          TeacherSubjectClassRepository teacherSubjectClassRepository,
                          StudentSubjectRepository studentSubjectRepository) {
        this.subjectRepository = subjectRepository;
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
    }

    public List<Subject> getSubjectsByCategory(SubjectCategory category) {
        return subjectRepository.findByCategory(category);
    }

    public List<Subject> getSubjectsByLevel(String level) {
        return subjectRepository.findByLevel(level);
    }

    public Subject createSubject(Subject subject) {
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, Subject subjectDetails) {
        Subject subject = getSubjectById(id);

        subject.setName(subjectDetails.getName());
        subject.setCode(subjectDetails.getCode());
        subject.setCategory(subjectDetails.getCategory());
        subject.setLevel(subjectDetails.getLevel());
        subject.setDescription(subjectDetails.getDescription());
        subject.setUpdatedAt(LocalDateTime.now());

        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    public List<Teacher> getTeachersBySubject(Long subjectId) {
        return teacherSubjectClassRepository.findBySubjectId(subjectId)
                .stream()
                .map(TeacherSubjectClass::getTeacher)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<StudentDTO> getStudentsBySubject(Long subjectId) {
        return studentSubjectRepository.findBySubjectId(subjectId)
                .stream()
                .map(ss -> {
                    Student student = ss.getStudent();
                    StudentDTO dto = new StudentDTO();
                    dto.setId(student.getId());
                    dto.setFirstName(student.getFirstName());
                    dto.setLastName(student.getLastName());
                    dto.setStudentId(student.getStudentId());
                    dto.setForm(student.getForm());
                    dto.setSection(student.getSection());
                    dto.setLevel(student.getLevel());
                    dto.setAcademicYear(student.getAcademicYear());
                    dto.setWhatsappNumber(student.getWhatsappNumber());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<String> getClassesBySubject(Long subjectId) {
        return teacherSubjectClassRepository.findBySubjectId(subjectId)
                .stream()
                .map(tsc -> tsc.getForm() + " " + tsc.getSection())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<TeacherSubjectAssignmentDTO> getTeachersWithClassesBySubject(Long subjectId) {
        List<TeacherSubjectClass> assignments = teacherSubjectClassRepository.findBySubjectId(subjectId);
        
        Map<Teacher, List<String>> teacherClassesMap = assignments.stream()
                .collect(Collectors.groupingBy(
                    TeacherSubjectClass::getTeacher,
                    Collectors.mapping(
                        tsc -> tsc.getForm() + " " + tsc.getSection(),
                        Collectors.toList()
                    )
                ));
        
        return teacherClassesMap.entrySet().stream()
                .map(entry -> {
                    Teacher teacher = entry.getKey();
                    List<String> classes = entry.getValue();
                    return new TeacherSubjectAssignmentDTO(
                        teacher.getId(),
                        teacher.getFirstName(),
                        teacher.getLastName(),
                        teacher.getEmployeeId(),
                        classes
                    );
                })
                .collect(Collectors.toList());
    }
}
