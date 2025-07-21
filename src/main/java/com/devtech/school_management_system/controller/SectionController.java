package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Section;
import com.devtech.school_management_system.service.SectionService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/sections", produces = MediaType.APPLICATION_JSON_VALUE)
public class SectionController {
    
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Section> getAllSections() {
        return sectionService.getAllSections();
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Section> getActiveSections() {
        return sectionService.getActiveSections();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Section getSectionById(@PathVariable Long id) {
        return sectionService.getSectionById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Section createSection(@RequestBody Section section) {
        return sectionService.createSection(section);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Section updateSection(@PathVariable Long id, @RequestBody Section sectionDetails) {
        return sectionService.updateSection(id, sectionDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
    }
}