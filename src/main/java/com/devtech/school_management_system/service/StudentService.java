package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.StudentUpdateDTO;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassGroupRepository classGroupRepository;
    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          ClassGroupRepository classGroupRepository,
                          SubjectRepository subjectRepository,
                          StudentSubjectRepository studentSubjectRepository) {
        this.studentRepository = studentRepository;
        this.classGroupRepository = classGroupRepository;
        this.subjectRepository = subjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student createStudent(String firstName, String lastName, String studentId,
                                 String form, String section, String level) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        student.setForm(form);
        student.setSection(section);
        student.setLevel(level);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, StudentUpdateDTO updateDTO) {
        Student student = getStudentById(id);

        if (updateDTO.getFirstName() != null) {
            student.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            student.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getForm() != null) {
            student.setForm(updateDTO.getForm());
        }
        if (updateDTO.getSection() != null) {
            student.setSection(updateDTO.getSection());
        }
        if (updateDTO.getLevel() != null) {
            student.setLevel(updateDTO.getLevel());
        }

        student.setUpdatedAt(LocalDateTime.now());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public List<Student> getStudentsByClass(String form, String section) {
        return studentRepository.findByFormAndSection(form, section);
    }

    public StudentSubject assignSubjectToStudent(Long studentId, Long subjectId) {
        Student student = getStudentById(studentId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        StudentSubject studentSubject = new StudentSubject();
        studentSubject.setStudent(student);
        studentSubject.setSubject(subject);
        studentSubject.setCreatedAt(LocalDateTime.now());
        studentSubject.setUpdatedAt(LocalDateTime.now());

        return studentSubjectRepository.save(studentSubject);
    }

    public void removeSubjectFromStudent(Long studentId, Long subjectId) {
        StudentSubject studentSubject = studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Student-Subject assignment not found"));

        studentSubjectRepository.delete(studentSubject);
    }

    public List<Subject> getStudentSubjects(Long studentId) {
        return studentSubjectRepository.findByStudentId(studentId)
                .stream()
                .map(StudentSubject::getSubject)
                .collect(Collectors.toList());
    }

    public List<Student> advanceStudentsToNextForm(List<Long> studentIds) {
        List<Student> students = studentRepository.findAllById(studentIds);

        for (Student student : students) {
            String currentForm = student.getForm();
            String nextForm = getNextForm(currentForm);
            student.setForm(nextForm);
            student.setUpdatedAt(LocalDateTime.now());
        }

        return studentRepository.saveAll(students);
    }

    public List<Student> promoteStudentsToALevel(List<Long> studentIds, List<Long> subjectIds,
                                                 String form, String section) {
        List<Student> students = studentRepository.findAllById(studentIds);
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);

        for (Student student : students) {
            student.setForm(form);
            student.setSection(section);
            student.setLevel("A_LEVEL");
            student.setUpdatedAt(LocalDateTime.now());

            // Assign A-Level subjects
            for (Subject subject : subjects) {
                StudentSubject studentSubject = new StudentSubject();
                studentSubject.setStudent(student);
                studentSubject.setSubject(subject);
                studentSubject.setCreatedAt(LocalDateTime.now());
                studentSubject.setUpdatedAt(LocalDateTime.now());
                studentSubjectRepository.save(studentSubject);
            }
        }

        return studentRepository.saveAll(students);
    }

    private String getNextForm(String currentForm) {
        switch (currentForm) {
            case "Form 1":
                return "Form 2";
            case "Form 2":
                return "Form 3";
            case "Form 3":
                return "Form 4";
            case "Form 4":
                return "Form 5";
            case "Form 5":
                return "Form 6";
            default:
                return currentForm;
        }
    }
}
