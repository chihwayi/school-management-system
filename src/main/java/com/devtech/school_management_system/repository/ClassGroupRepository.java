package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {

    Optional<ClassGroup> findByFormAndSectionAndAcademicYear(String form, String section, String academicYear);

    List<ClassGroup> findByAcademicYear(String academicYear);

    List<ClassGroup> findByForm(String form);

    @Query("SELECT c FROM ClassGroup c WHERE c.classTeacher.id = :teacherId")
    List<ClassGroup> findByClassTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT c FROM ClassGroup c WHERE c.form LIKE :form% ORDER BY c.form, c.section")
    List<ClassGroup> findByFormLevel(@Param("form") String form);

    List<ClassGroup> findByLevel(String level);

    @Query("SELECT c FROM ClassGroup c LEFT JOIN FETCH c.classTeacher")
    List<ClassGroup> findAllWithClassTeachers();
}
