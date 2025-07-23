package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    
    List<FeePayment> findByStudentIdAndTermAndAcademicYear(Long studentId, String term, String academicYear);
    
    Optional<FeePayment> findByStudentIdAndTermAndMonthAndAcademicYear(Long studentId, String term, String month, String academicYear);
    
    @Query("SELECT fp FROM FeePayment fp JOIN fp.student s WHERE s.form = :form AND s.section = :section AND fp.paymentStatus = :status")
    List<FeePayment> findByClassAndPaymentStatus(@Param("form") String form, @Param("section") String section, @Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(fp.amountPaid) FROM FeePayment fp WHERE fp.paymentDate = :date")
    BigDecimal findTotalAmountByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(fp) FROM FeePayment fp WHERE fp.paymentDate = :date")
    Long findTotalTransactionsByDate(@Param("date") LocalDate date);
    
    List<FeePayment> findByPaymentDate(LocalDate date);
    
    List<FeePayment> findByStudentId(Long studentId);
    
    List<FeePayment> findByTermAndAcademicYear(String term, String academicYear);
    
    List<FeePayment> findByAcademicYear(String academicYear);
    
    @Query("SELECT fp FROM FeePayment fp WHERE fp.paymentDate BETWEEN :startDate AND :endDate")
    List<FeePayment> findByPaymentDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT fp FROM FeePayment fp WHERE fp.term = :term AND fp.academicYear = :academicYear AND fp.balance > 0")
    List<FeePayment> findOutstandingPaymentsByTermAndAcademicYear(@Param("term") String term, @Param("academicYear") String academicYear);
}