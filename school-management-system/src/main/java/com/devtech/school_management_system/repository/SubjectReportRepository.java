package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.SubjectReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectReportRepository extends JpaRepository<SubjectReport, Long> {

    List<SubjectReport> findByReportId(Long reportId);

    Optional<SubjectReport> findByReportIdAndSubjectId(Long reportId, Long subjectId);

    @Query("SELECT sr FROM SubjectReport sr WHERE sr.teacher.id = :teacherId")
    List<SubjectReport> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT sr FROM SubjectReport sr WHERE sr.subject.id = :subjectId AND sr.report.term = :term AND sr.report.academicYear = :year")
    List<SubjectReport> findBySubjectIdAndTermAndYear(@Param("subjectId") Long subjectId,
                                                      @Param("term") String term,
                                                      @Param("year") String year);
}
