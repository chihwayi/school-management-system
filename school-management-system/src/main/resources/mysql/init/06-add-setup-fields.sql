-- =====================================================
-- Add Setup Completion Tracking Fields
-- Version: 1.1.0
-- =====================================================

USE school_management_system;

-- Add setup completion tracking fields to schools table
ALTER TABLE schools 
ADD COLUMN IF NOT EXISTS setup_completed BOOLEAN DEFAULT FALSE AFTER configured,
ADD COLUMN IF NOT EXISTS setup_step VARCHAR(50) DEFAULT 'basic' AFTER setup_completed,
ADD COLUMN IF NOT EXISTS requires_setup BOOLEAN DEFAULT TRUE AFTER setup_step;

-- Update existing schools to require setup if they only have basic config
UPDATE schools 
SET requires_setup = TRUE, 
    setup_completed = FALSE,
    setup_step = 'basic'
WHERE logo_path IS NULL OR background_path IS NULL;

-- Update schema version
INSERT IGNORE INTO schema_version (version, description, checksum) VALUES 
('1.1.0', 'Add setup completion tracking fields', 'setup_fields_checksum');