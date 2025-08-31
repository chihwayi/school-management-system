package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    List<Student> findByFormAndSection(String form, String section);

    @Query("SELECT s FROM Student s WHERE s.form = :form AND s.section = :section AND s.academicYear = :year")
    List<Student> findByFormAndSectionAndYear(@Param("form") String form,
                                              @Param("section") String section,
                                              @Param("year") String year);
                                              
    List<Student> findByFormAndSectionAndAcademicYear(String form, String section, String academicYear);

    List<Student> findByLevel(String level);

    List<Student> findByForm(String form);

    @Query("SELECT s FROM Student s WHERE s.firstName LIKE %:name% OR s.lastName LIKE %:name%")
    List<Student> findByNameContaining(@Param("name") String name);
    
    Optional<Student> findByWhatsappNumber(String whatsappNumber);
}

