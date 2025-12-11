package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtEntity.NqtLoginAttempt;
import k23cnt1.nqt.project3.nqtRepository.NqtLoginAttemptRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtSettingRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NqtRateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(NqtRateLimitService.class);

    @Autowired
    private NqtLoginAttemptRepository loginAttemptRepository;

    @Autowired
    private NqtSettingRepository settingRepository;

    // Default configuration values (can be overridden by settings)
    private static final int DEFAULT_MAX_FAILED_ATTEMPTS = 5; // Max failed attempts before lockout
    private static final int DEFAULT_LOCKOUT_DURATION_MINUTES = 15; // Lockout duration in minutes
    private static final int DEFAULT_RATE_LIMIT_ATTEMPTS = 10; // Max attempts per time window
    private static final int DEFAULT_RATE_LIMIT_WINDOW_MINUTES = 15; // Time window in minutes
    private static final int DEFAULT_IP_RATE_LIMIT_ATTEMPTS = 20; // Max attempts per IP
    private static final int DEFAULT_IP_RATE_LIMIT_WINDOW_MINUTES = 15; // Time window for IP
    
    // Action types
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_REGISTER = "register";
    public static final String ACTION_FORGOT_PASSWORD = "forgot_password";
    public static final String ACTION_RESET_PASSWORD = "reset_password";
    public static final String ACTION_EMAIL_VERIFICATION = "email_verification";
    public static final String ACTION_2FA_VERIFICATION = "2fa_verification";

    /**
     * Check if identifier (username/email) is locked due to too many failed attempts
     * Only counts failed LOGIN attempts, not other action types
     */
    public RateLimitResult checkBruteForceProtection(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return new RateLimitResult(false, "Invalid identifier");
        }

        int maxFailedAttempts = getSettingAsInt("rate_limit_max_failed_attempts", DEFAULT_MAX_FAILED_ATTEMPTS);
        int lockoutDuration = getSettingAsInt("rate_limit_lockout_duration_minutes", DEFAULT_LOCKOUT_DURATION_MINUTES);

        LocalDateTime since = LocalDateTime.now().minusMinutes(lockoutDuration);
        // Only count failed LOGIN attempts for brute force protection
        long failedCount = loginAttemptRepository.countFailedAttemptsByIdentifierAndAction(identifier, ACTION_LOGIN, since);

        logger.info("🔒 Brute force check for '{}': {} failed login attempts in last {} minutes (max: {})", 
            identifier, failedCount, lockoutDuration, maxFailedAttempts);

        if (failedCount >= maxFailedAttempts) {
            // Get the most recent failed login attempt to calculate unlock time
            List<NqtLoginAttempt> attempts = loginAttemptRepository.findFailedAttemptsByIdentifierAndAction(identifier, ACTION_LOGIN, since);
            
            if (!attempts.isEmpty()) {
                // Sort by time descending (most recent first)
                attempts.sort((a1, a2) -> a2.getNqtAttemptTime().compareTo(a1.getNqtAttemptTime()));
                LocalDateTime lastAttempt = attempts.get(0).getNqtAttemptTime();
                LocalDateTime unlockTime = lastAttempt.plusMinutes(lockoutDuration);
                
                if (LocalDateTime.now().isBefore(unlockTime)) {
                    long remainingMinutes = java.time.Duration.between(LocalDateTime.now(), unlockTime).toMinutes() + 1;
                    logger.warn("🚫 Account '{}' LOCKED due to {} failed login attempts. Unlocks in {} minutes", 
                        identifier, failedCount, remainingMinutes);
                    return new RateLimitResult(true, 
                        "Tài khoản đã bị khóa tạm thời do quá nhiều lần đăng nhập sai (" + failedCount + " lần). " +
                        "Vui lòng thử lại sau " + remainingMinutes + " phút.");
                } else {
                    // Lockout period has passed, account should be unlocked
                    logger.info("✅ Account '{}' lockout period expired, account unlocked", identifier);
                }
            }
        }

        return new RateLimitResult(false, null);
    }

    /**
     * Check rate limiting for identifier (username/email)
     * Only counts FAILED attempts, not successful ones
     */
    public RateLimitResult checkRateLimitByIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return new RateLimitResult(false, "Invalid identifier");
        }

        int maxAttempts = getSettingAsInt("rate_limit_max_attempts", DEFAULT_RATE_LIMIT_ATTEMPTS);
        int windowMinutes = getSettingAsInt("rate_limit_window_minutes", DEFAULT_RATE_LIMIT_WINDOW_MINUTES);

        LocalDateTime since = LocalDateTime.now().minusMinutes(windowMinutes);
        // Only count FAILED attempts for rate limiting
        long failedAttemptCount = loginAttemptRepository.countFailedAttemptsByIdentifier(identifier, since);

        if (failedAttemptCount >= maxAttempts) {
            return new RateLimitResult(true, 
                "Quá nhiều lần thử đăng nhập sai. Vui lòng đợi " + windowMinutes + " phút trước khi thử lại.");
        }

        return new RateLimitResult(false, null);
    }

    /**
     * Check rate limiting for IP address
     * Only counts FAILED attempts, not successful ones
     */
    public RateLimitResult checkRateLimitByIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return new RateLimitResult(false, "Invalid IP address");
        }

        int maxAttempts = getSettingAsInt("rate_limit_ip_max_attempts", DEFAULT_IP_RATE_LIMIT_ATTEMPTS);
        int windowMinutes = getSettingAsInt("rate_limit_ip_window_minutes", DEFAULT_IP_RATE_LIMIT_WINDOW_MINUTES);

        LocalDateTime since = LocalDateTime.now().minusMinutes(windowMinutes);
        // Only count FAILED attempts for rate limiting
        long failedAttemptCount = loginAttemptRepository.countFailedAttemptsByIpAddress(ipAddress, since);

        if (failedAttemptCount >= maxAttempts) {
            return new RateLimitResult(true, 
                "Quá nhiều lần thử đăng nhập sai từ địa chỉ IP này. Vui lòng đợi " + windowMinutes + " phút trước khi thử lại.");
        }

        return new RateLimitResult(false, null);
    }

    /**
     * Record a login attempt
     */
    @Transactional
    public void recordLoginAttempt(String identifier, String ipAddress, boolean success, 
                                   String failureReason, String userAgent) {
        recordAttempt(identifier, ipAddress, success, failureReason, userAgent, ACTION_LOGIN);
    }
    
    /**
     * Record an attempt for any action type
     */
    @Transactional
    public void recordAttempt(String identifier, String ipAddress, boolean success, 
                              String failureReason, String userAgent, String actionType) {
        NqtLoginAttempt attempt = new NqtLoginAttempt();
        attempt.setNqtIdentifier(identifier);
        attempt.setNqtIpAddress(ipAddress);
        attempt.setNqtSuccess(success);
        attempt.setNqtFailureReason(failureReason);
        attempt.setNqtUserAgent(userAgent);
        attempt.setNqtActionType(actionType != null ? actionType : ACTION_LOGIN); // Default to login if null
        attempt.setNqtAttemptTime(LocalDateTime.now());
        
        loginAttemptRepository.save(attempt);
        
        if (!success) {
            logger.debug("❌ Recorded failed {} attempt for '{}' from IP {}: {}", 
                attempt.getNqtActionType(), identifier, ipAddress, failureReason);
        } else {
            logger.debug("✅ Recorded successful {} attempt for '{}' from IP {}", 
                attempt.getNqtActionType(), identifier, ipAddress);
        }
    }

    /**
     * Clear failed attempts for an identifier (called after successful login)
     */
    @Transactional
    public void clearFailedAttempts(String identifier) {
        // We don't delete, but successful login will naturally reduce the count
        // when checking within the time window
    }

    /**
     * Get client IP address from request
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Handle multiple IPs (X-Forwarded-For can contain multiple IPs)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }

    /**
     * Get user agent from request
     */
    public String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.length() > 500) {
            userAgent = userAgent.substring(0, 500);
        }
        return userAgent;
    }

    /**
     * Clean up old login attempts (should be called periodically via scheduler)
     */
    @Transactional
    public void cleanupOldAttempts(int daysToKeep) {
        LocalDateTime before = LocalDateTime.now().minusDays(daysToKeep);
        loginAttemptRepository.deleteOldAttempts(before);
    }

    /**
     * Scheduled task to clean up old login attempts (runs daily at 2 AM)
     * Keeps attempts for 30 days by default
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void scheduledCleanupOldAttempts() {
        try {
            logger.info("Bắt đầu dọn dẹp các login attempts cũ...");
            int daysToKeep = getSettingAsInt("rate_limit_cleanup_days", 30);
            LocalDateTime before = LocalDateTime.now().minusDays(daysToKeep);
            
            long countBefore = loginAttemptRepository.count();
            loginAttemptRepository.deleteOldAttempts(before);
            long countAfter = loginAttemptRepository.count();
            long deletedCount = countBefore - countAfter;
            
            logger.info("Hoàn thành dọn dẹp login attempts. Đã xóa {} bản ghi cũ hơn {} ngày.", deletedCount, daysToKeep);
        } catch (Exception e) {
            logger.error("Lỗi khi dọn dẹp login attempts cũ", e);
        }
    }

    /**
     * Check rate limiting for specific action type by identifier
     */
    public RateLimitResult checkRateLimitByIdentifierAndAction(String identifier, String actionType, 
                                                               String defaultMessage) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return new RateLimitResult(false, "Invalid identifier");
        }

        int maxAttempts = getSettingAsInt("rate_limit_max_attempts", DEFAULT_RATE_LIMIT_ATTEMPTS);
        int windowMinutes = getSettingAsInt("rate_limit_window_minutes", DEFAULT_RATE_LIMIT_WINDOW_MINUTES);

        LocalDateTime since = LocalDateTime.now().minusMinutes(windowMinutes);
        long attemptCount = loginAttemptRepository.countTotalAttemptsByIdentifierAndAction(identifier, actionType, since);

        if (attemptCount >= maxAttempts) {
            return new RateLimitResult(true, 
                defaultMessage != null ? defaultMessage : 
                "Quá nhiều lần thử. Vui lòng đợi " + windowMinutes + " phút trước khi thử lại.");
        }

        return new RateLimitResult(false, null);
    }

    /**
     * Check rate limiting for specific action type by IP address
     */
    public RateLimitResult checkRateLimitByIpAddressAndAction(String ipAddress, String actionType,
                                                              String defaultMessage) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return new RateLimitResult(false, "Invalid IP address");
        }

        int maxAttempts = getSettingAsInt("rate_limit_ip_max_attempts", DEFAULT_IP_RATE_LIMIT_ATTEMPTS);
        int windowMinutes = getSettingAsInt("rate_limit_ip_window_minutes", DEFAULT_IP_RATE_LIMIT_WINDOW_MINUTES);

        LocalDateTime since = LocalDateTime.now().minusMinutes(windowMinutes);
        long attemptCount = loginAttemptRepository.countTotalAttemptsByIpAddressAndAction(ipAddress, actionType, since);

        if (attemptCount >= maxAttempts) {
            return new RateLimitResult(true, 
                defaultMessage != null ? defaultMessage :
                "Quá nhiều lần thử từ địa chỉ IP này. Vui lòng đợi " + windowMinutes + " phút trước khi thử lại.");
        }

        return new RateLimitResult(false, null);
    }

    /**
     * Get setting value as integer
     */
    private int getSettingAsInt(String settingName, int defaultValue) {
        return settingRepository.findByNqtName(settingName)
            .map(setting -> {
                try {
                    return Integer.parseInt(setting.getNqtValue());
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            })
            .orElse(defaultValue);
    }

    /**
     * Result class for rate limit checks
     */
    public static class RateLimitResult {
        private final boolean blocked;
        private final String message;

        public RateLimitResult(boolean blocked, String message) {
            this.blocked = blocked;
            this.message = message;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public String getMessage() {
            return message;
        }
    }
}

