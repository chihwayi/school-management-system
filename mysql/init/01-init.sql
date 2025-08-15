-- Create database if not exists
CREATE DATABASE IF NOT EXISTS school_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE school_management_system;

-- Create a dedicated user for the application (optional, for better security)
CREATE USER IF NOT EXISTS 'school_user'@'%' IDENTIFIED BY 'school_password';
GRANT ALL PRIVILEGES ON school_management_system.* TO 'school_user'@'%';
FLUSH PRIVILEGES;

-- Set timezone
SET time_zone = '+00:00';
