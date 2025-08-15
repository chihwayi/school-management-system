package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByDate(LocalDate date);

    Optional<Attendance> findByStudentIdAndDate(Long studentId, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByStudentIdAndDateRange(@Param("studentId") Long studentId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.student.form = :form AND a.student.section = :section AND a.date = :date")
    List<Attendance> findByStudentFormAndStudentSectionAndDate(@Param("form") String form,
                                                               @Param("section") String section,
                                                               @Param("date") LocalDate date);

    Optional<Attendance> findByStudentAndDate(com.devtech.school_management_system.entity.Student student, LocalDate date);

    List<Attendance> findByStudent(com.devtech.school_management_system.entity.Student student);

    List<Attendance> findByStudentAndDateBetween(com.devtech.school_management_system.entity.Student student, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByDateAndPresentFalse(LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.student.form = :form AND a.student.section = :section AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByStudentFormAndStudentSectionAndDateBetween(@Param("form") String form,
                                                                      @Param("section") String section,
                                                                      @Param("startDate") LocalDate startDate,
                                                                      @Param("endDate") LocalDate endDate);
    
    void deleteByStudentId(Long studentId);
}
