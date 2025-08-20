-- =====================================================
-- School Management System - Seed Data
-- Version: 1.0.0
-- Date: 2025-08-17
-- =====================================================

USE school_management_system;

-- =====================================================
-- 1. INSERT ROLES
-- =====================================================
INSERT IGNORE INTO roles (name) VALUES 
('ROLE_ADMIN'),
('ROLE_CLERK'),
('ROLE_TEACHER'),
('ROLE_CLASS_TEACHER');

-- =====================================================
-- 2. INSERT USERS
-- =====================================================
INSERT IGNORE INTO users (username, email, password, first_name, last_name) VALUES 
('admin', 'admin@school.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System', 'Administrator'),
('clerk', 'clerk@school.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'School', 'Clerk'),
('teacher', 'teacher@school.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Default', 'Teacher'),
('classteacher', 'classteacher@school.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Class', 'Teacher');

-- =====================================================
-- 3. ASSIGN ROLES TO USERS
-- =====================================================
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- admin -> ROLE_ADMIN
(1, 3), -- admin -> ROLE_TEACHER
(2, 2), -- clerk -> ROLE_CLERK
(3, 3), -- teacher -> ROLE_TEACHER
(4, 3), -- classteacher -> ROLE_TEACHER
(4, 4); -- classteacher -> ROLE_CLASS_TEACHER

-- =====================================================
-- 4. INSERT SECTIONS
-- =====================================================
INSERT IGNORE INTO sections (name, description) VALUES 
('Green', 'Green Section'),
('Blue', 'Blue Section'),
('Red', 'Red Section'),
('Yellow', 'Yellow Section');

-- =====================================================
-- 5. INSERT SUBJECTS
-- =====================================================
INSERT IGNORE INTO subjects (name, code, category, description) VALUES 
('English', 'ENG', 'Languages & Humanities', 'English Language and Literature'),
('Mathematics', 'MATH', 'Sciences', 'Mathematics and Problem Solving'),
('Biology', 'BIO', 'Sciences', 'Biological Sciences'),
('Chemistry', 'CHEM', 'Sciences', 'Chemical Sciences'),
('Physics', 'PHY', 'Sciences', 'Physical Sciences'),
('History', 'HIST', 'Languages & Humanities', 'Historical Studies'),
('Geography', 'GEO', 'Sciences', 'Geographical Studies'),
('Indigenous Language', 'IND', 'Languages & Humanities', 'Local Language Studies'),
('Principles of Accounting', 'ACC', 'Commercials', 'Accounting Principles'),
('Commerce', 'COM', 'Commercials', 'Business and Commerce'),
('Business Enterprise Skills', 'BES', 'Commercials', 'Business Skills Development'),
('Economics', 'ECO', 'Commercials', 'Economic Studies'),
('Heritage Studies', 'HER', 'Languages & Humanities', 'Cultural Heritage'),
('Literature in English', 'LIT', 'Languages & Humanities', 'English Literature'),
('Combined Science', 'CS', 'Sciences', 'General Science');

-- =====================================================
-- 6. INSERT TEACHERS
-- =====================================================
INSERT IGNORE INTO teachers (first_name, last_name, employee_id, user_id) VALUES 
('System', 'Administrator', 'ADMIN001', 1),
('Default', 'Teacher', 'TCH001', 3),
('Class', 'Teacher', 'TCH002', 4);

-- =====================================================
-- 7. INSERT STUDENTS
-- =====================================================
INSERT IGNORE INTO students (first_name, last_name, student_id, form, section, level, academic_year, enrollment_date) VALUES 
('Kaylee', 'Chihwayi', 'STU001', 'Form 1', 'Green', 'O_LEVEL', '2025', '2025-01-15'),
('John', 'Doe', 'STU002', 'Form 1', 'Green', 'O_LEVEL', '2025', '2025-01-15'),
('Jane', 'Smith', 'STU003', 'Form 1', 'Green', 'O_LEVEL', '2025', '2025-01-15'),
('Michael', 'Johnson', 'STU004', 'Form 2', 'Blue', 'O_LEVEL', '2025', '2025-01-15'),
('Sarah', 'Williams', 'STU005', 'Form 2', 'Blue', 'O_LEVEL', '2025', '2025-01-15'),
('David', 'Brown', 'STU006', 'Form 3', 'Red', 'O_LEVEL', '2025', '2025-01-15'),
('Emily', 'Davis', 'STU007', 'Form 3', 'Red', 'O_LEVEL', '2025', '2025-01-15'),
('Robert', 'Wilson', 'STU008', 'Form 4', 'Yellow', 'O_LEVEL', '2025', '2025-01-15'),
('Lisa', 'Anderson', 'STU009', 'Form 4', 'Yellow', 'O_LEVEL', '2025', '2025-01-15'),
('James', 'Taylor', 'STU010', 'Form 5', 'Green', 'A_LEVEL', '2025', '2025-01-15');

-- =====================================================
-- 8. INSERT GUARDIANS
-- =====================================================
INSERT IGNORE INTO guardians (name, relationship, phone_number, whatsapp_number, primary_guardian, student_id) VALUES 
('Mr. Chihwayi', 'Father', '+263778886543', '+263778886543', TRUE, 1),
('Mrs. Chihwayi', 'Mother', '+263778886544', '+263778886544', FALSE, 1),
('Mr. Doe', 'Father', '+263778886545', '+263778886545', TRUE, 2),
('Mrs. Doe', 'Mother', '+263778886546', '+263778886546', FALSE, 2),
('Mr. Smith', 'Father', '+263778886547', '+263778886547', TRUE, 3),
('Mrs. Smith', 'Mother', '+263778886548', '+263778886548', FALSE, 3),
('Mr. Johnson', 'Father', '+263778886549', '+263778886549', TRUE, 4),
('Mrs. Johnson', 'Mother', '+263778886550', '+263778886550', FALSE, 4),
('Mr. Williams', 'Father', '+263778886551', '+263778886551', TRUE, 5),
('Mrs. Williams', 'Mother', '+263778886552', '+263778886552', FALSE, 5);

-- =====================================================
-- 9. INSERT CLASS GROUPS
-- =====================================================
INSERT IGNORE INTO class_groups (form, section, academic_year, class_teacher_id) VALUES 
('Form 1', 'Green', '2025', 3),
('Form 1', 'Blue', '2025', 3),
('Form 2', 'Green', '2025', 3),
('Form 2', 'Blue', '2025', 4),
('Form 3', 'Red', '2025', 4),
('Form 4', 'Yellow', '2025', 4);

-- =====================================================
-- 10. INSERT TEACHER SUBJECT CLASS ASSIGNMENTS
-- =====================================================
INSERT IGNORE INTO teacher_subject_class (teacher_id, subject_id, form, section, academic_year) VALUES 
-- Default Teacher assignments
(3, 1, 'Form 1', 'Green', '2025'), -- English
(3, 2, 'Form 1', 'Green', '2025'), -- Mathematics
(3, 3, 'Form 1', 'Green', '2025'), -- Biology
(3, 4, 'Form 1', 'Green', '2025'), -- Chemistry
(3, 5, 'Form 1', 'Green', '2025'), -- Physics

-- Class Teacher assignments
(4, 1, 'Form 2', 'Blue', '2025'), -- English
(4, 2, 'Form 2', 'Blue', '2025'), -- Mathematics
(4, 6, 'Form 2', 'Blue', '2025'), -- History
(4, 7, 'Form 2', 'Blue', '2025'), -- Geography
(4, 8, 'Form 2', 'Blue', '2025'); -- Indigenous Language

-- =====================================================
-- 11. INSERT STUDENT SUBJECT ENROLLMENTS
-- =====================================================
INSERT IGNORE INTO student_subjects (student_id, subject_id) VALUES 
-- Form 1 Green students
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 15), -- Kaylee
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 15), -- John
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 15), -- Jane

-- Form 2 Blue students
(4, 1), (4, 2), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10), (4, 11), (4, 12), -- Michael
(5, 1), (5, 2), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10), (5, 11), (5, 12); -- Sarah

-- =====================================================
-- 12. INSERT FEE SETTINGS
-- =====================================================
INSERT IGNORE INTO fee_settings (level, amount, academic_year, term, active) VALUES 
('O_LEVEL', 100.00, '2025', 'Term 1', TRUE),
('O_LEVEL', 100.00, '2025', 'Term 2', TRUE),
('O_LEVEL', 100.00, '2025', 'Term 3', TRUE),
('A_LEVEL', 150.00, '2025', 'Term 1', TRUE),
('A_LEVEL', 150.00, '2025', 'Term 2', TRUE),
('A_LEVEL', 150.00, '2025', 'Term 3', TRUE);

-- =====================================================
-- 13. INSERT SAMPLE ATTENDANCE (Last 30 days)
-- =====================================================
-- This will be populated by the application as attendance is marked

-- =====================================================
-- 14. INSERT SAMPLE REPORTS (Will be created by application)
-- =====================================================
-- Reports are created dynamically by the application

-- =====================================================
-- 15. INSERT SAMPLE ASSESSMENTS (Will be created by application)
-- =====================================================
-- Assessments are created dynamically by the application

-- =====================================================
-- 16. INSERT SAMPLE FEE PAYMENTS (Will be created by application)
-- =====================================================
-- Fee payments are created dynamically by the application

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check if data was inserted correctly
SELECT 'Roles' as table_name, COUNT(*) as count FROM roles
UNION ALL
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Sections' as table_name, COUNT(*) as count FROM sections
UNION ALL
SELECT 'Subjects' as table_name, COUNT(*) as count FROM subjects
UNION ALL
SELECT 'Teachers' as table_name, COUNT(*) as count FROM teachers
UNION ALL
SELECT 'Students' as table_name, COUNT(*) as count FROM students
UNION ALL
SELECT 'Guardians' as table_name, COUNT(*) as count FROM guardians
UNION ALL
SELECT 'Class Groups' as table_name, COUNT(*) as count FROM class_groups
UNION ALL
SELECT 'Teacher Assignments' as table_name, COUNT(*) as count FROM teacher_subject_class
UNION ALL
SELECT 'Student Enrollments' as table_name, COUNT(*) as count FROM student_subjects
UNION ALL
SELECT 'Fee Settings' as table_name, COUNT(*) as count FROM fee_settings;
