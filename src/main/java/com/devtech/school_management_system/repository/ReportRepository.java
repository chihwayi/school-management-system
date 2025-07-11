package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStudentId(Long studentId);
/*
    Optional<Report> findByStudentIdAndTermAndAcademicYear(Long studentId, String term, String academicYear);

    @Query("SELECT r FROM Report r WHERE r.student.form = :form AND r.student.section = :section AND r.term = :term AND r.academicYear = :year")
    List<Report> findByFormAndSectionAndTermAndAcademicYear(@Param("form") String form,
                                                            @Param("section") String section,
                                                            @Param("term") String term,
                                                            @Param("year") String year);
*/
    @Query("SELECT r FROM Report r WHERE r.classTeacher.id = :teacherId")
    List<Report> findByClassTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT r FROM Report r WHERE r.finalized = :finalized")
    List<Report> findByFinalized(@Param("finalized") boolean finalized);

    Optional<Report> findByStudentIdAndTermAndAcademicYear(Long studentId, String term, String academicYear);

    @Query("SELECT r FROM Report r WHERE r.student.form = :form AND r.student.section = :section AND r.term = :term AND r.academicYear = :year")
    List<Report> findByFormAndSectionAndTermAndAcademicYear(@Param("form") String form,
                                                            @Param("section") String section,
                                                            @Param("term") String term,
                                                            @Param("year") String year);
}
