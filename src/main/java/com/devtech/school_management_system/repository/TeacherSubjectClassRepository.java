package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.TeacherSubjectClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSubjectClassRepository extends JpaRepository<TeacherSubjectClass, Long> {
    
    void deleteByTeacherId(Long teacherId);

    List<TeacherSubjectClass> findByTeacherId(Long teacherId);

    List<TeacherSubjectClass> findBySubjectId(Long subjectId);

    @Query("SELECT tsc FROM TeacherSubjectClass tsc WHERE tsc.form = :form AND tsc.section = :section AND tsc.academicYear = :year")
    List<TeacherSubjectClass> findByFormAndSectionAndYear(@Param("form") String form,
                                                          @Param("section") String section,
                                                          @Param("year") String year);

    Optional<TeacherSubjectClass> findBySubjectIdAndFormAndSectionAndAcademicYear(Long subjectId, String form, String section, String academicYear);

    @Query("SELECT tsc FROM TeacherSubjectClass tsc WHERE tsc.teacher.id = :teacherId AND tsc.subject.id = :subjectId")
    List<TeacherSubjectClass> findByTeacherIdAndSubjectId(@Param("teacherId") Long teacherId,
                                                          @Param("subjectId") Long subjectId);

    @Query("SELECT CASE WHEN COUNT(tsc) > 0 THEN true ELSE false END FROM TeacherSubjectClass tsc WHERE tsc.teacher.id = :teacherId AND tsc.subject.id = :subjectId AND tsc.form = :form AND tsc.section = :section")
    boolean existsByEmployeeIdAndSubjectIdAndFormAndSection(@Param("teacherId") Long teacherId,
                                                            @Param("subjectId") Long subjectId,
                                                            @Param("form") String form,
                                                            @Param("section") String section);

    @Query("SELECT CASE WHEN COUNT(tsc) > 0 THEN true ELSE false END FROM TeacherSubjectClass tsc JOIN StudentSubject ss ON ss.subject.id = tsc.subject.id AND ss.student.form = tsc.form AND ss.student.section = tsc.section WHERE tsc.teacher.id = :teacherId AND ss.id = :studentSubjectId")
    boolean existsByTeacherIdAndStudentSubjectId(@Param("teacherId") Long teacherId,
                                                 @Param("studentSubjectId") Long studentSubjectId);

    @Query("SELECT CASE WHEN COUNT(tsc) > 0 THEN true ELSE false END FROM TeacherSubjectClass tsc WHERE tsc.teacher.id = :teacherId AND tsc.subject.id = :subjectId")
    boolean existsByTeacherIdAndSubjectId(@Param("teacherId") Long teacherId,
                                          @Param("subjectId") Long subjectId);
}
