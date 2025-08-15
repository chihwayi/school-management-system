package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    // School is typically a single entity, so we mainly need basic CRUD
}
