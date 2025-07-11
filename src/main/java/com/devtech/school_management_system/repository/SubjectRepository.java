package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.enums.SubjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByName(String name);

    List<Subject> findByCategory(SubjectCategory category);

    List<Subject> findByLevel(String level);

    List<Subject> findByCategoryAndLevel(SubjectCategory category, String level);

    boolean existsByName(String name);
}
