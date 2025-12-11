-- Alter nqtsetting table to change nqtValue column from VARCHAR(5000) to TEXT
-- This allows storing longer HTML content for support pages

-- Check if column exists and alter it
SET @dbname = DATABASE();
SET @tablename = 'nqtsetting';
SET @columnname = 'nqtValue';

SET @preparedStatement = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE
            (table_name = @tablename)
            AND (table_schema = @dbname)
            AND (column_name = @columnname)
            AND (data_type = 'varchar' OR character_maximum_length < 65535)
    ) > 0,
    CONCAT('ALTER TABLE ', @tablename, ' MODIFY COLUMN ', @columnname, ' TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci'),
    'SELECT 1'
));

PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

