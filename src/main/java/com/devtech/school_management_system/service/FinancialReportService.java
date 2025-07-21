package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.enums.PaymentStatus;
import com.devtech.school_management_system.repository.FeePaymentRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        List<FeePayment> allPayments = feePaymentRepository.findByTermAndAcademicYear(term, academicYear);
        if (allPayments == null) {
            allPayments = new ArrayList<>();
        }

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
                .collect(Collectors.groupingBy(p -> p.getStudent().getForm() + " " + p.getStudent().getSection()));

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
                .map(date -> {
                    try {
                        return feePaymentService.getDailyPaymentSummary(date);
                    } catch (Exception e) {
                        return new DailyPaymentSummaryDTO(date, BigDecimal.ZERO, 0L);
                    }
                })
                .filter(summary -> summary.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        report.setDailySummaries(dailySummaries);

        return report;
    }
    
    public List<StudentPaymentHistoryDTO> getAllStudentPaymentHistory() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(this::createStudentPaymentHistoryDTO)
                .collect(Collectors.toList());
    }
    
    public List<StudentPaymentHistoryDTO> getStudentPaymentHistory(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        return Collections.singletonList(createStudentPaymentHistoryDTO(student));
    }
    
    private StudentPaymentHistoryDTO createStudentPaymentHistoryDTO(Student student) {
        List<FeePayment> payments = feePaymentRepository.findByStudentId(student.getId());
        if (payments == null) {
            payments = new ArrayList<>();
        }
        
        List<StudentPaymentHistoryDTO.PaymentRecord> paymentRecords = payments.stream()
                .map(payment -> new StudentPaymentHistoryDTO.PaymentRecord(
                        payment.getTerm(),
                        payment.getMonth(),
                        payment.getAcademicYear(),
                        payment.getAmountPaid(),
                        payment.getBalance(),
                        payment.getPaymentDate(),
                        payment.getPaymentStatus()
                ))
                .collect(Collectors.toList());
        
        BigDecimal totalPaid = payments.stream()
                .map(FeePayment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalBalance = payments.stream()
                .map(FeePayment::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new StudentPaymentHistoryDTO(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getForm() + " " + student.getSection(),
                totalPaid,
                paymentRecords,
                totalBalance
        );
    }
    
    public List<PaymentTrendDTO> getPaymentTrends(LocalDate startDate, LocalDate endDate) {
        List<FeePayment> payments = feePaymentRepository.findAll();
        if (payments == null) {
            payments = new ArrayList<>();
        }
        
        Map<LocalDate, List<FeePayment>> paymentsByDate = payments.stream()
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(FeePayment::getPaymentDate));
        
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    List<FeePayment> paymentsOnDate = paymentsByDate.getOrDefault(date, Collections.emptyList());
                    
                    BigDecimal totalAmount = paymentsOnDate.stream()
                            .map(FeePayment::getAmountPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return new PaymentTrendDTO(date, totalAmount, paymentsOnDate.size());
                })
                .filter(trend -> trend.getTransactionCount() > 0)
                .collect(Collectors.toList());
    }
    
    public List<ClassComparisonDTO> getClassComparison(String academicYear) {
        List<FeePayment> payments = feePaymentRepository.findByAcademicYear(academicYear);
        if (payments == null) {
            payments = new ArrayList<>();
        }
        
        Map<String, List<FeePayment>> paymentsByClass = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getStudent().getForm() + " " + p.getStudent().getSection()));
        
        return paymentsByClass.entrySet().stream()
                .map(entry -> {
                    String className = entry.getKey();
                    List<FeePayment> classPayments = entry.getValue();
                    
                    // Count unique students
                    long totalStudents = classPayments.stream()
                            .map(p -> p.getStudent().getId())
                            .distinct()
                            .count();
                    
                    BigDecimal totalCollected = classPayments.stream()
                            .map(FeePayment::getAmountPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal totalOutstanding = classPayments.stream()
                            .map(FeePayment::getBalance)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal totalExpected = totalCollected.add(totalOutstanding);
                    
                    // Calculate collection rate as percentage
                    double collectionRate = totalExpected.compareTo(BigDecimal.ZERO) > 0 
                            ? totalCollected.divide(totalExpected, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    
                    // Calculate average payment per student
                    BigDecimal averagePerStudent = totalStudents > 0 
                            ? totalCollected.divide(BigDecimal.valueOf(totalStudents), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    
                    return new ClassComparisonDTO(
                            className,
                            totalCollected,
                            totalStudents,
                            totalOutstanding,
                            collectionRate,
                            averagePerStudent
                    );
                })
                .collect(Collectors.toList());
    }
    
    public List<FeePayment> getOutstandingPayments(String term, String academicYear) {
        List<FeePayment> payments = feePaymentRepository.findByTermAndAcademicYear(term, academicYear);
        if (payments == null) {
            payments = new ArrayList<>();
        }
        
        return payments.stream()
                .filter(payment -> payment.getPaymentStatus() != PaymentStatus.FULL_PAYMENT)
                .collect(Collectors.toList());
    }
    
    public List<AuditLogDTO> getPaymentAuditLogs(LocalDate startDate, LocalDate endDate) {
        // This is a mock implementation since we don't have a real audit log table
        // In a real application, you would query from an audit log repository
        List<FeePayment> payments = feePaymentRepository.findAll();
        if (payments == null) {
            payments = new ArrayList<>();
        }
        
        payments = payments.stream()
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
        
        List<AuditLogDTO> auditLogs = new ArrayList<>();
        long id = 1;
        
        for (FeePayment payment : payments) {
            AuditLogDTO log = new AuditLogDTO();
            log.setId(id++);
            log.setAction("PAYMENT_RECORDED");
            log.setDescription("Payment recorded for student " + payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName());
            log.setPerformedBy("System Admin");
            log.setTimestamp(payment.getCreatedAt());
            log.setPaymentId(payment.getId());
            log.setStudentId(payment.getStudent().getId());
            log.setAmount(payment.getAmountPaid());
            auditLogs.add(log);
        }
        
        return auditLogs;
    }
    
    public byte[] exportAllPaymentsToExcel(String term, String academicYear) {
        List<FeePayment> payments = feePaymentRepository.findByTermAndAcademicYear(term, academicYear);
        if (payments == null) {
            payments = new ArrayList<>();
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("All Payments");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Student ID", "Student Name", "Class", "Term", "Month", "Academic Year", 
                               "Fee Amount", "Amount Paid", "Balance", "Payment Status", "Payment Date"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
            
            // Create data rows
            int rowNum = 1;
            for (FeePayment payment : payments) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(payment.getStudent().getStudentId());
                row.createCell(1).setCellValue(payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName());
                row.createCell(2).setCellValue(payment.getStudent().getForm() + " " + payment.getStudent().getSection());
                row.createCell(3).setCellValue(payment.getTerm());
                row.createCell(4).setCellValue(payment.getMonth());
                row.createCell(5).setCellValue(payment.getAcademicYear());
                row.createCell(6).setCellValue(payment.getMonthlyFeeAmount().doubleValue());
                row.createCell(7).setCellValue(payment.getAmountPaid().doubleValue());
                row.createCell(8).setCellValue(payment.getBalance().doubleValue());
                row.createCell(9).setCellValue(payment.getPaymentStatus().toString());
                row.createCell(10).setCellValue(payment.getPaymentDate().format(DateTimeFormatter.ISO_DATE));
            }
            
            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }
    
    public byte[] exportStudentPaymentHistoryToExcel(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        List<FeePayment> payments = feePaymentRepository.findByStudentId(studentId);
        if (payments == null) {
            payments = new ArrayList<>();
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payment History");
            
            // Create student info section
            Row studentInfoRow = sheet.createRow(0);
            studentInfoRow.createCell(0).setCellValue("Student Name:");
            studentInfoRow.createCell(1).setCellValue(student.getFirstName() + " " + student.getLastName());
            
            Row classInfoRow = sheet.createRow(1);
            classInfoRow.createCell(0).setCellValue("Class:");
            classInfoRow.createCell(1).setCellValue(student.getForm() + " " + student.getSection());
            
            // Create header row
            Row headerRow = sheet.createRow(3);
            String[] columns = {"Term", "Month", "Academic Year", "Fee Amount", "Amount Paid", 
                               "Balance", "Payment Status", "Payment Date"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
            
            // Create data rows
            int rowNum = 4;
            for (FeePayment payment : payments) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(payment.getTerm());
                row.createCell(1).setCellValue(payment.getMonth());
                row.createCell(2).setCellValue(payment.getAcademicYear());
                row.createCell(3).setCellValue(payment.getMonthlyFeeAmount().doubleValue());
                row.createCell(4).setCellValue(payment.getAmountPaid().doubleValue());
                row.createCell(5).setCellValue(payment.getBalance().doubleValue());
                row.createCell(6).setCellValue(payment.getPaymentStatus().toString());
                row.createCell(7).setCellValue(payment.getPaymentDate().format(DateTimeFormatter.ISO_DATE));
            }
            
            // Add summary section
            BigDecimal totalPaid = payments.stream()
                    .map(FeePayment::getAmountPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalBalance = payments.stream()
                    .map(FeePayment::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Row summaryHeaderRow = sheet.createRow(rowNum + 1);
            summaryHeaderRow.createCell(0).setCellValue("Summary");
            
            Row totalPaidRow = sheet.createRow(rowNum + 2);
            totalPaidRow.createCell(0).setCellValue("Total Paid:");
            totalPaidRow.createCell(1).setCellValue(totalPaid.doubleValue());
            
            Row totalBalanceRow = sheet.createRow(rowNum + 3);
            totalBalanceRow.createCell(0).setCellValue("Total Balance:");
            totalBalanceRow.createCell(1).setCellValue(totalBalance.doubleValue());
            
            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }
}