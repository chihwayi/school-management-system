package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.enums.PaymentStatus;
import com.devtech.school_management_system.repository.FeePaymentRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinancialReportService {
    
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;
    private final FeePaymentService feePaymentService;

    public FinancialReportService(FeePaymentRepository feePaymentRepository, 
                                StudentRepository studentRepository,
                                FeePaymentService feePaymentService) {
        this.feePaymentRepository = feePaymentRepository;
        this.studentRepository = studentRepository;
        this.feePaymentService = feePaymentService;
    }

    public FinancialReportDTO generateFinancialReport(String term, String academicYear, 
                                                    LocalDate startDate, LocalDate endDate) {
        FinancialReportDTO report = new FinancialReportDTO();
        report.setReportDate(LocalDate.now());
        report.setTerm(term);
        report.setAcademicYear(academicYear);

        // Get all payments for the period
        List<FeePayment> allPayments = feePaymentRepository.findAll().stream()
                .filter(p -> p.getTerm().equals(term) && p.getAcademicYear().equals(academicYear))
                .collect(Collectors.toList());

        // Calculate totals
        BigDecimal totalCollected = allPayments.stream()
                .map(FeePayment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = allPayments.stream()
                .map(FeePayment::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpected = totalCollected.add(totalOutstanding);

        report.setTotalCollectedAmount(totalCollected);
        report.setTotalOutstandingAmount(totalOutstanding);
        report.setTotalExpectedRevenue(totalExpected);

        // Generate class summaries
        Map<String, List<FeePayment>> paymentsByClass = allPayments.stream()
                .collect(Collectors.groupingBy(p -> p.getStudent().getClassName()));

        List<ClassFinancialSummaryDTO> classSummaries = paymentsByClass.entrySet().stream()
                .map(entry -> {
                    String className = entry.getKey();
                    List<FeePayment> classPayments = entry.getValue();
                    
                    long fullPayments = classPayments.stream()
                            .filter(p -> p.getPaymentStatus() == PaymentStatus.FULL_PAYMENT)
                            .count();
                    
                    long partPayments = classPayments.stream()
                            .filter(p -> p.getPaymentStatus() == PaymentStatus.PART_PAYMENT)
                            .count();
                    
                    long nonPayers = classPayments.stream()
                            .filter(p -> p.getPaymentStatus() == PaymentStatus.NON_PAYER)
                            .count();

                    BigDecimal classCollected = classPayments.stream()
                            .map(FeePayment::getAmountPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal classOutstanding = classPayments.stream()
                            .map(FeePayment::getBalance)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new ClassFinancialSummaryDTO(className, (long) classPayments.size(),
                            fullPayments, partPayments, nonPayers, classCollected, classOutstanding);
                })
                .collect(Collectors.toList());

        report.setClassSummaries(classSummaries);

        // Generate daily summaries for the date range
        List<DailyPaymentSummaryDTO> dailySummaries = startDate.datesUntil(endDate.plusDays(1))
                .map(feePaymentService::getDailyPaymentSummary)
                .filter(summary -> summary.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        report.setDailySummaries(dailySummaries);

        return report;
    }
}