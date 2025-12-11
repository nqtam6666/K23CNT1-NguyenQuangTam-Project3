-- Add admin_path setting to nqtsetting table
-- This allows admin to customize the admin panel path for security

-- Check if admin_path setting already exists, if not, insert it
SET @dbname = DATABASE();
SET @tablename = 'nqtsetting';
SET @settingname = 'admin_path';
SET @settingvalue = 'admin';

SET @preparedStatement = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
        WHERE table_schema = @dbname AND table_name = @tablename
    ) > 0,
    IF(
        (
            SELECT COUNT(*) FROM `nqtsetting`
            WHERE `nqtName` = @settingname
        ) > 0,
        'SELECT 1',
        CONCAT('INSERT INTO `', @tablename, '` (`nqtName`, `nqtValue`) VALUES (''', @settingname, ''', ''', @settingvalue, ''')')
    ),
    'SELECT 1'
));

PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

