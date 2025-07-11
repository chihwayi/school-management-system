package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.enums.SubjectCategory;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
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
}
