package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByActiveTrue();
    Optional<Section> findByName(String name);
    boolean existsByName(String name);
}