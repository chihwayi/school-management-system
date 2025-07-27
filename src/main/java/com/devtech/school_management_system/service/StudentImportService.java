package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Section;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.repository.SectionRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class StudentImportService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SectionRepository sectionRepository;

    public Resource generateTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "First Name*", "Last Name*", "Student ID*", "Form*", "Section*", 
            "Level*", "Academic Year*", "Date of Birth", "Gender", "Address", 
            "Phone", "Email", "Guardian Name", "Guardian Phone", "Guardian Relationship"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Add sample data row
        Row sampleRow = sheet.createRow(1);
        String[] sampleData = {
            "John", "Doe", "STU001", "Form 1", "A", "JUNIOR_SECONDARY", "2025",
            "2010-01-15", "Male", "123 Main St", "+263771234567", "john.doe@email.com",
            "Jane Doe", "+263771234568", "Mother"
        };

        for (int i = 0; i < sampleData.length; i++) {
            sampleRow.createCell(i).setCellValue(sampleData[i]);
        }

        // Add instructions sheet
        Sheet instructionsSheet = workbook.createSheet("Instructions");
        Row instrRow = instructionsSheet.createRow(0);
        instrRow.createCell(0).setCellValue("STUDENT IMPORT INSTRUCTIONS");
        
        String[] instructions = {
            "",
            "Required Fields (marked with *):",
            "- First Name, Last Name, Student ID, Form, Section, Level, Academic Year",
            "",
            "Level Options:",
            "- JUNIOR_SECONDARY (Forms 1-2)",
            "- O_LEVEL (Forms 3-4)", 
            "- A_LEVEL (Forms 5-6)",
            "",
            "Form Format: 'Form 1', 'Form 2', etc.",
            "Section: Single letter (A, B, C, etc.)",
            "Academic Year: 4-digit year (2025)",
            "Date Format: YYYY-MM-DD",
            "",
            "Notes:",
            "- Sections will be created automatically if they don't exist",
            "- Duplicate Student IDs will be skipped",
            "- Guardian information is optional"
        };

        for (int i = 0; i < instructions.length; i++) {
            Row row = instructionsSheet.createRow(i + 1);
            row.createCell(0).setCellValue(instructions[i]);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    public Map<String, Object> importStudents(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<String> errors = new ArrayList<>();
        List<Student> successfulImports = new ArrayList<>();
        int totalRows = sheet.getLastRowNum();
        int processedRows = 0;

        for (int i = 1; i <= totalRows; i++) { // Skip header row
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Student student = processRow(row, i + 1);
                if (student != null) {
                    successfulImports.add(student);
                }
                processedRows++;
            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
            }
        }

        workbook.close();

        Map<String, Object> result = new HashMap<>();
        result.put("totalRows", totalRows);
        result.put("processedRows", processedRows);
        result.put("successfulImports", successfulImports.size());
        result.put("errors", errors);
        result.put("students", successfulImports);

        return result;
    }

    private Student processRow(Row row, int rowNumber) {
        // Required fields
        String firstName = getCellValue(row, 0);
        String lastName = getCellValue(row, 1);
        String studentId = getCellValue(row, 2);
        String form = getCellValue(row, 3);
        String sectionName = getCellValue(row, 4);
        String level = getCellValue(row, 5);
        String academicYear = getCellValue(row, 6);

        // Validate required fields
        if (isEmpty(firstName) || isEmpty(lastName) || isEmpty(studentId) || 
            isEmpty(form) || isEmpty(sectionName) || isEmpty(level) || isEmpty(academicYear)) {
            throw new RuntimeException("Missing required fields");
        }

        // Check for duplicate student ID
        if (studentRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Student ID already exists: " + studentId);
        }

        // Create or get section
        Section section = sectionRepository.findByName(sectionName)
                .orElseGet(() -> {
                    Section newSection = new Section();
                    newSection.setName(sectionName);
                    newSection.setDescription("Section " + sectionName);
                    newSection.setActive(true);
                    return sectionRepository.save(newSection);
                });

        // Create student
        Student student = new Student();
        student.setFirstName(firstName.trim());
        student.setLastName(lastName.trim());
        student.setStudentId(studentId.trim());
        student.setForm(form.trim());
        student.setSection(sectionName.trim());
        student.setLevel(level.trim());
        student.setAcademicYear(academicYear.trim());

        // Optional fields
        String dobStr = getCellValue(row, 7);
        if (!isEmpty(dobStr)) {
            try {
                student.setDateOfBirth(LocalDate.parse(dobStr));
            } catch (Exception e) {
                // Skip invalid date
            }
        }

        student.setGender(getCellValue(row, 8));
        student.setAddress(getCellValue(row, 9));
        student.setPhone(getCellValue(row, 10));
        student.setEmail(getCellValue(row, 11));
        student.setEnrollmentDate(LocalDate.now());

        return studentRepository.save(student);
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}