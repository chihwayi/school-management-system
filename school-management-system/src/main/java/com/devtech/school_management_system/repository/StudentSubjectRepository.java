package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    List<StudentSubject> findByStudentId(Long studentId);

    List<StudentSubject> findBySubjectId(Long subjectId);

    Optional<StudentSubject> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    @Query("SELECT ss FROM StudentSubject ss WHERE ss.student.id = :studentId AND ss.academicYear = :year")
    List<StudentSubject> findByStudentIdAndAcademicYear(@Param("studentId") Long studentId,
                                                        @Param("year") String academicYear);

    @Query("SELECT ss FROM StudentSubject ss WHERE ss.subject.id = :subjectId AND ss.student.form = :form AND ss.student.section = :section")
    List<StudentSubject> findBySubjectIdAndFormAndSection(@Param("subjectId") Long subjectId,
                                                          @Param("form") String form,
                                                          @Param("section") String section);
    
    void deleteByStudentId(Long studentId);
}
