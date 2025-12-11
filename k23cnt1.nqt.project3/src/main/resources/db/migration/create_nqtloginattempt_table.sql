-- Create table for login attempts tracking (Rate Limiting & Brute Force Protection)
CREATE TABLE IF NOT EXISTS `nqtloginattempt` (
  `nqtId` INT AUTO_INCREMENT PRIMARY KEY,
  `nqtIdentifier` VARCHAR(255) NOT NULL COMMENT 'Username, email, or user ID',
  `nqtIpAddress` VARCHAR(45) COMMENT 'IPv4 or IPv6 address',
  `nqtAttemptTime` DATETIME NOT NULL COMMENT 'Time of login attempt',
  `nqtSuccess` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'true if login successful, false if failed',
  `nqtFailureReason` VARCHAR(500) COMMENT 'Reason for failure (wrong password, account locked, etc.)',
  `nqtUserAgent` VARCHAR(500) COMMENT 'Browser/device info',
  `nqtActionType` VARCHAR(50) COMMENT 'Action type: login, register, forgot_password, reset_password, email_verification, 2fa_verification',
  INDEX `idx_identifier` (`nqtIdentifier`),
  INDEX `idx_ip_address` (`nqtIpAddress`),
  INDEX `idx_attempt_time` (`nqtAttemptTime`),
  INDEX `idx_action_type` (`nqtActionType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Login attempts tracking for rate limiting and brute force protection';

