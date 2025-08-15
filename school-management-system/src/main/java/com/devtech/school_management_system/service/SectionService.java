package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Section;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionService {
    
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public List<Section> getActiveSections() {
        return sectionRepository.findByActiveTrue();
    }

    public Section getSectionById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));
    }

    public Section createSection(Section section) {
        if (sectionRepository.existsByName(section.getName())) {
            throw new RuntimeException("Section with name '" + section.getName() + "' already exists");
        }
        return sectionRepository.save(section);
    }

    public Section updateSection(Long id, Section sectionDetails) {
        Section section = getSectionById(id);
        
        if (!section.getName().equals(sectionDetails.getName()) && 
            sectionRepository.existsByName(sectionDetails.getName())) {
            throw new RuntimeException("Section with name '" + sectionDetails.getName() + "' already exists");
        }
        
        section.setName(sectionDetails.getName());
        section.setDescription(sectionDetails.getDescription());
        section.setActive(sectionDetails.getActive());
        
        return sectionRepository.save(section);
    }

    public void deleteSection(Long id) {
        Section section = getSectionById(id);
        section.setActive(false);
        sectionRepository.save(section);
    }
}