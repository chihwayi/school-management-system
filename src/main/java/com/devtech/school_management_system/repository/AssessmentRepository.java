package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByStudentSubjectId(Long studentSubjectId);

    @Query("SELECT a FROM Assessment a WHERE a.studentSubject.student.id = :studentId AND a.studentSubject.subject.id = :subjectId")
    List<Assessment> findByStudentIdAndSubjectId(@Param("studentId") Long studentId,
                                                 @Param("subjectId") Long subjectId);

    @Query("SELECT a FROM Assessment a WHERE a.studentSubject.student.id = :studentId AND a.term = :term AND a.academicYear = :year")
    List<Assessment> findByStudentIdAndTermAndAcademicYear(@Param("studentId") Long studentId,
                                                           @Param("term") String term,
                                                           @Param("year") String year);

    @Query("SELECT a FROM Assessment a WHERE a.studentSubject.student.id = :studentId AND a.studentSubject.subject.id = :subjectId AND a.term = :term AND a.academicYear = :year")
    List<Assessment> findByStudentSubjectTermAndYear(@Param("studentId") Long studentId,
                                                     @Param("subjectId") Long subjectId,
                                                     @Param("term") String term,
                                                     @Param("year") String year);

    @Query("SELECT a FROM Assessment a WHERE a.studentSubject.student.id = :studentId AND a.studentSubject.subject.id = :subjectId")
    List<Assessment> findByStudentSubjectStudentIdAndStudentSubjectSubjectId(@Param("studentId") Long studentId,
                                                                             @Param("subjectId") Long subjectId);

    @Query("SELECT a FROM Assessment a WHERE a.studentSubject.student.id = :studentId AND a.term = :term AND a.academicYear = :year")
    List<Assessment> findByStudentSubjectStudentIdAndTermAndAcademicYear(@Param("studentId") Long studentId,
                                                                         @Param("term") String term,
                                                                         @Param("year") String year);

    @Query("SELECT a FROM Assessment a WHERE a.studentSubject.id = :studentSubjectId AND a.type = :type AND a.term = :term AND a.academicYear = :year")
    List<Assessment> findByStudentSubjectIdAndTypeAndTermAndAcademicYear(@Param("studentSubjectId") Long studentSubjectId,
                                                                         @Param("type") com.devtech.school_management_system.enums.AssessmentType type,
                                                                         @Param("term") String term,
                                                                         @Param("year") String year);
}
