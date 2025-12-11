-- Safe script to add nqtActionType column (checks if column exists first)
-- This uses a stored procedure approach that works in MySQL

DELIMITER $$

DROP PROCEDURE IF EXISTS AddNqtActionTypeColumn$$

CREATE PROCEDURE AddNqtActionTypeColumn()
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    
    -- Check if column exists
    SELECT COUNT(*) INTO column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'nqtloginattempt'
    AND COLUMN_NAME = 'nqtActionType';
    
    -- Add column if it doesn't exist
    IF column_exists = 0 THEN
        ALTER TABLE `nqtloginattempt` 
        ADD COLUMN `nqtActionType` VARCHAR(50) COMMENT 'Action type: login, register, forgot_password, reset_password, email_verification, 2fa_verification' AFTER `nqtUserAgent`;
    END IF;
    
    -- Check if index exists
    SET column_exists = 0;
    SELECT COUNT(*) INTO column_exists
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'nqtloginattempt'
    AND INDEX_NAME = 'idx_action_type';
    
    -- Add index if it doesn't exist
    IF column_exists = 0 THEN
        ALTER TABLE `nqtloginattempt` 
        ADD INDEX `idx_action_type` (`nqtActionType`);
    END IF;
END$$

DELIMITER ;

-- Execute the procedure
CALL AddNqtActionTypeColumn();

-- Clean up
DROP PROCEDURE IF EXISTS AddNqtActionTypeColumn;

