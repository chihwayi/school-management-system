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
import java.util.Objects;

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
        try {
            FinancialReportDTO report = new FinancialReportDTO();
            report.setReportDate(LocalDate.now());
            report.setTerm(term);
            report.setAcademicYear(academicYear);
    
            // Get all payments for the period
            List<FeePayment> allPayments = new ArrayList<>();
            
            // First get payments by term and academic year
            List<FeePayment> termPayments = feePaymentRepository.findByTermAndAcademicYear(term, academicYear);
            if (termPayments != null) {
                allPayments.addAll(termPayments);
            }
            
            // Also get payments by date range
            List<FeePayment> dateRangePayments = feePaymentRepository.findByPaymentDateBetween(startDate, endDate);
            if (dateRangePayments != null) {
                // Add any payments not already included
                for (FeePayment payment : dateRangePayments) {
                    if (!allPayments.contains(payment)) {
                        allPayments.add(payment);
                    }
                }
            }
            
            System.out.println("Found " + allPayments.size() + " payments for report");
    
            // Filter out invalid payments
            List<FeePayment> validPayments = allPayments.stream()
                .filter(p -> p != null && p.getStudent() != null && 
                       p.getAmountPaid() != null && p.getBalance() != null)
                .collect(Collectors.toList());
    
            // Calculate totals
            BigDecimal totalCollected = validPayments.stream()
                    .map(FeePayment::getAmountPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    
            BigDecimal totalOutstanding = validPayments.stream()
                    .map(FeePayment::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    
            BigDecimal totalExpected = totalCollected.add(totalOutstanding);
    
            report.setTotalCollectedAmount(totalCollected);
            report.setTotalOutstandingAmount(totalOutstanding);
            report.setTotalExpectedRevenue(totalExpected);
    
            // Generate class summaries - only for payments with valid student data
            Map<String, List<FeePayment>> paymentsByClass = validPayments.stream()
                    .filter(p -> p.getStudent().getForm() != null && p.getStudent().getSection() != null)
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
        } catch (Exception e) {
            System.err.println("Error generating financial report: " + e.getMessage());
            e.printStackTrace();
            // Return a minimal valid report instead of throwing an exception
            FinancialReportDTO emptyReport = new FinancialReportDTO();
            emptyReport.setReportDate(LocalDate.now());
            emptyReport.setTerm(term);
            emptyReport.setAcademicYear(academicYear);
            emptyReport.setTotalCollectedAmount(BigDecimal.ZERO);
            emptyReport.setTotalOutstandingAmount(BigDecimal.ZERO);
            emptyReport.setTotalExpectedRevenue(BigDecimal.ZERO);
            emptyReport.setClassSummaries(new ArrayList<>());
            emptyReport.setDailySummaries(new ArrayList<>());
            return emptyReport;
        }
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
        
        // Use the actual student ID (student_id) instead of the database ID
        String actualStudentId = student.getStudentId();
        
        // For the DTO, we'll still use the database ID since that's what the system expects
        return new StudentPaymentHistoryDTO(
                student.getId(),
                student.getFirstName() + " " + student.getLastName() + " (" + actualStudentId + ")",
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
        try {
            List<FeePayment> payments = feePaymentRepository.findByAcademicYear(academicYear);
            if (payments == null) {
                payments = new ArrayList<>();
            }
            
            // Filter out payments with null students or missing form/section
            List<FeePayment> validPayments = payments.stream()
                .filter(p -> p != null && p.getStudent() != null && 
                       p.getStudent().getForm() != null && 
                       p.getStudent().getSection() != null)
                .collect(Collectors.toList());
            
            if (validPayments.isEmpty()) {
                return new ArrayList<>();
            }
            
            Map<String, List<FeePayment>> paymentsByClass = validPayments.stream()
                    .collect(Collectors.groupingBy(p -> p.getStudent().getForm() + " " + p.getStudent().getSection()));
            
            return paymentsByClass.entrySet().stream()
                    .map(entry -> {
                        try {
                            String className = entry.getKey();
                            List<FeePayment> classPayments = entry.getValue();
                            
                            // Count unique students
                            long totalStudents = classPayments.stream()
                                    .map(p -> p.getStudent().getId())
                                    .filter(Objects::nonNull)
                                    .distinct()
                                    .count();
                            
                            BigDecimal totalCollected = classPayments.stream()
                                    .map(p -> p.getAmountPaid() != null ? p.getAmountPaid() : BigDecimal.ZERO)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            
                            BigDecimal totalOutstanding = classPayments.stream()
                                    .map(p -> p.getBalance() != null ? p.getBalance() : BigDecimal.ZERO)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            
                            BigDecimal totalExpected = totalCollected.add(totalOutstanding);
                            
                            // Calculate collection rate as percentage
                            double collectionRate = 0.0;
                            if (totalExpected.compareTo(BigDecimal.ZERO) > 0) {
                                try {
                                    collectionRate = totalCollected.divide(totalExpected, 4, RoundingMode.HALF_UP)
                                            .multiply(BigDecimal.valueOf(100))
                                            .doubleValue();
                                } catch (Exception e) {
                                    // Handle any division errors
                                    collectionRate = 0.0;
                                }
                            }
                            
                            // Calculate average payment per student
                            BigDecimal averagePerStudent = BigDecimal.ZERO;
                            if (totalStudents > 0) {
                                try {
                                    averagePerStudent = totalCollected.divide(BigDecimal.valueOf(totalStudents), 2, RoundingMode.HALF_UP);
                                } catch (Exception e) {
                                    // Handle any division errors
                                    averagePerStudent = BigDecimal.ZERO;
                                }
                            }
                            
                            return new ClassComparisonDTO(
                                    className,
                                    totalCollected,
                                    totalStudents,
                                    totalOutstanding,
                                    collectionRate,
                                    averagePerStudent
                            );
                        } catch (Exception e) {
                            // If processing a specific class fails, return a default object
                            System.err.println("Error processing class data: " + e.getMessage());
                            return new ClassComparisonDTO(
                                    entry.getKey(),
                                    BigDecimal.ZERO,
                                    0L,
                                    BigDecimal.ZERO,
                                    0.0,
                                    BigDecimal.ZERO
                            );
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error in getClassComparison: " + e.getMessage());
            e.printStackTrace();
            // Return empty list instead of throwing exception
            return new ArrayList<>();
        }
    }
    
    public List<FeePayment> getOutstandingPayments(String term, String academicYear) {
        try {
            // Use the repository method to get outstanding payments
            List<FeePayment> results = feePaymentRepository.findOutstandingPaymentsByTermAndAcademicYear(term, academicYear);
            
            if (results == null) {
                results = new ArrayList<>();
            }
                
            System.out.println("Found " + results.size() + " outstanding payments");
            return results;
        } catch (Exception e) {
            System.err.println("Error in getOutstandingPayments: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<AuditLogDTO> getPaymentAuditLogs(LocalDate startDate, LocalDate endDate) {
        List<AuditLogDTO> auditLogs = new ArrayList<>();
        
        try {
            // Get payments directly by date range
            List<FeePayment> payments = feePaymentRepository.findByPaymentDateBetween(startDate, endDate);
            if (payments == null || payments.isEmpty()) {
                // If no payments found in date range, get all payments and filter manually
                payments = feePaymentRepository.findAll();
                
                if (payments != null) {
                    payments = payments.stream()
                        .filter(p -> p != null && p.getPaymentDate() != null)
                        .filter(p -> !p.getPaymentDate().isBefore(startDate) && !p.getPaymentDate().isAfter(endDate))
                        .collect(Collectors.toList());
                } else {
                    payments = new ArrayList<>();
                }
            }
            
            System.out.println("Found " + payments.size() + " payments for audit logs between " + 
                             startDate + " and " + endDate);
            
            // Create audit logs from payments
            long id = 1;
            for (FeePayment payment : payments) {
                if (payment == null || payment.getStudent() == null) continue;
                
                try {
                    AuditLogDTO log = new AuditLogDTO();
                    log.setId(id++);
                    log.setAction("PAYMENT_RECORDED");
                    log.setDescription("Payment of $" + payment.getAmountPaid() + " recorded for student " + 
                                      payment.getStudent().getFirstName() + " " + 
                                      payment.getStudent().getLastName());
                    log.setPerformedBy("System Admin");
                    
                    // Use payment date if createdAt is null
                    log.setTimestamp(payment.getCreatedAt() != null ? 
                                    payment.getCreatedAt() : 
                                    LocalDateTime.of(payment.getPaymentDate(), java.time.LocalTime.NOON));
                    
                    log.setPaymentId(payment.getId());
                    log.setStudentId(payment.getStudent().getId());
                    log.setAmount(payment.getAmountPaid());
                    auditLogs.add(log);
                } catch (Exception e) {
                    System.err.println("Error creating audit log for payment " + payment.getId() + ": " + e.getMessage());
                }
            }
            
            // If still no data, create some sample data
            if (auditLogs.isEmpty()) {
                System.out.println("No audit logs found, creating sample data");
                AuditLogDTO sampleLog = new AuditLogDTO();
                sampleLog.setId(1L);
                sampleLog.setAction("SYSTEM_MESSAGE");
                sampleLog.setDescription("No payment records found in the selected date range");
                sampleLog.setPerformedBy("System");
                sampleLog.setTimestamp(LocalDateTime.now());
                sampleLog.setAmount(BigDecimal.ZERO);
                auditLogs.add(sampleLog);
            }
            
            return auditLogs;
        } catch (Exception e) {
            System.err.println("Error in getPaymentAuditLogs: " + e.getMessage());
            e.printStackTrace();
            
            // Return at least one log entry with the error
            AuditLogDTO errorLog = new AuditLogDTO();
            errorLog.setId(1L);
            errorLog.setAction("ERROR");
            errorLog.setDescription("Error retrieving audit logs: " + e.getMessage());
            errorLog.setPerformedBy("System");
            errorLog.setTimestamp(LocalDateTime.now());
            errorLog.setAmount(BigDecimal.ZERO);
            auditLogs.add(errorLog);
            
            return auditLogs;
        }
    }
    
    public byte[] exportAllPaymentsToExcel(String term, String academicYear) {
        try {
            List<FeePayment> payments = feePaymentRepository.findByTermAndAcademicYear(term, academicYear);
            if (payments == null) {
                payments = new ArrayList<>();
            }
            
            // Filter out invalid payments
            List<FeePayment> validPayments = payments.stream()
                .filter(p -> p != null && p.getStudent() != null && 
                       p.getAmountPaid() != null && p.getBalance() != null && 
                       p.getMonthlyFeeAmount() != null && p.getPaymentStatus() != null && 
                       p.getPaymentDate() != null)
                .collect(Collectors.toList());
            
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
                for (FeePayment payment : validPayments) {
                    try {
                        Row row = sheet.createRow(rowNum++);
                        
                        // Use the actual student ID field (student_id) instead of the database ID
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
                    } catch (Exception e) {
                        // Skip this row if there's an error
                        System.err.println("Error processing payment row: " + e.getMessage());
                    }
                }
                
                // Auto-size columns
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // Write to byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            System.err.println("Failed to generate Excel file: " + e.getMessage());
            e.printStackTrace();
            
            // Return an empty Excel file instead of throwing an exception
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("No Data");
                Row row = sheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("No payment data available for the selected term and academic year.");
                
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();
            } catch (IOException ex) {
                // If all else fails, return an empty byte array
                return new byte[0];
            }
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