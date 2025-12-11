package k23cnt1.nqt.project3.nqtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to manage dynamic admin path
 * Allows admin to change the admin path to prevent unauthorized access
 */
@Service
public class NqtAdminPathService {
    
    private static final String DEFAULT_ADMIN_PATH = "admin";
    private static final String SETTING_NAME = "admin_path";
    
    @Autowired
    private NqtSettingService nqtSettingService;
    
    /**
     * Get the current admin path from settings
     * @return Admin path (default: "admin")
     */
    public String getAdminPath() {
        String path = nqtSettingService.getNqtValue(SETTING_NAME, DEFAULT_ADMIN_PATH);
        // Sanitize path: remove leading/trailing slashes and ensure it's valid
        if (path != null) {
            path = path.trim();
            // Remove leading slash
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            // Remove trailing slash
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            // Validate: only allow alphanumeric, dash, underscore
            if (path.matches("^[a-zA-Z0-9_-]+$") && !path.isEmpty()) {
                return path;
            }
        }
        return DEFAULT_ADMIN_PATH;
    }
    
    /**
     * Get admin path with leading slash
     * @return Admin path with leading slash (e.g., "/admin")
     */
    public String getAdminPathWithSlash() {
        return "/" + getAdminPath();
    }
    
    /**
     * Get admin path pattern for security config (with /**)
     * @return Admin path pattern (e.g., "/admin/**")
     */
    public String getAdminPathPattern() {
        return getAdminPathWithSlash() + "/**";
    }
    
    /**
     * Get admin login path
     * @return Admin login path (e.g., "/admin/login")
     */
    public String getAdminLoginPath() {
        return getAdminPathWithSlash() + "/login";
    }
    
    /**
     * Save admin path to settings
     * @param path New admin path
     * @return true if valid and saved, false otherwise
     */
    public boolean setAdminPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        // Sanitize
        path = path.trim();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        // Validate: only allow alphanumeric, dash, underscore
        if (!path.matches("^[a-zA-Z0-9_-]+$") || path.isEmpty()) {
            return false;
        }
        
        // Don't allow reserved paths
        String lowerPath = path.toLowerCase();
        if (lowerPath.equals("api") || lowerPath.equals("oauth2") || 
            lowerPath.equals("login") || lowerPath.equals("static") ||
            lowerPath.equals("uploads") || lowerPath.equals("nqt") ||
            lowerPath.equals("favicon.ico")) {
            return false;
        }
        
        nqtSettingService.saveNqtValue(SETTING_NAME, path);
        return true;
    }
}

