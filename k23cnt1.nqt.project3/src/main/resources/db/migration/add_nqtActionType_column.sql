-- Add nqtActionType column to existing nqtloginattempt table
-- This script can be run safely even if the column already exists (will show error but won't break)

-- Check if column exists, if not add it
-- Note: MySQL doesn't support IF NOT EXISTS in ALTER TABLE, so we need to handle errors manually
-- Run this script only if the column doesn't exist yet

ALTER TABLE `nqtloginattempt` 
ADD COLUMN `nqtActionType` VARCHAR(50) COMMENT 'Action type: login, register, forgot_password, reset_password, email_verification, 2fa_verification' AFTER `nqtUserAgent`;

-- Add index for nqtActionType if it doesn't exist
-- Note: MySQL doesn't support IF NOT EXISTS for indexes in ALTER TABLE
-- If index already exists, this will show an error but won't break
ALTER TABLE `nqtloginattempt` 
ADD INDEX `idx_action_type` (`nqtActionType`);

