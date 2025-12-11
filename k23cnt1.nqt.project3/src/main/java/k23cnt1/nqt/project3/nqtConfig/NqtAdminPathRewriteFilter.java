package k23cnt1.nqt.project3.nqtConfig;

import k23cnt1.nqt.project3.nqtService.NqtAdminPathService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to rewrite admin path from dynamic path to /admin
 * This allows controllers to use hardcoded /admin/** mappings
 * while supporting dynamic admin paths
 */
@Component
@Order(1) // Execute early, before Spring Security
public class NqtAdminPathRewriteFilter extends OncePerRequestFilter {

    @Autowired
    private NqtAdminPathService adminPathService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String adminPath = adminPathService.getAdminPathWithSlash();
        String defaultAdminPath = "/admin";
        
        // Block access to /admin/** if admin path has been changed
        if (!adminPath.equals(defaultAdminPath) && requestURI.startsWith(defaultAdminPath + "/")) {
            // Exclude static resources (css, js, images) from blocking
            if (!requestURI.startsWith(defaultAdminPath + "/css/") &&
                !requestURI.startsWith(defaultAdminPath + "/js/") &&
                !requestURI.startsWith(defaultAdminPath + "/images/")) {
                // Block access - redirect to the new admin path or return 404
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                    "<!DOCTYPE html>" +
                    "<html><head><meta charset='UTF-8'><title>404 - Không tìm thấy</title>" +
                    "<style>body{font-family:Arial,sans-serif;text-align:center;padding:50px;}" +
                    "h1{color:#e74a3b;}</style></head><body>" +
                    "<h1>404 - Không tìm thấy</h1>" +
                    "<p>Trang này không tồn tại. Vui lòng sử dụng đường dẫn admin mới.</p>" +
                    "</body></html>"
                );
                return;
            }
        }
        
        // Only rewrite if admin path is different from default and request matches admin path
        if (!adminPath.equals(defaultAdminPath) && requestURI.startsWith(adminPath)) {
            // Rewrite URI from dynamic admin path to /admin
            String rewrittenURI = requestURI.replaceFirst(adminPath, defaultAdminPath);
            
            // Create a wrapper to override getRequestURI and getServletPath
            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getRequestURI() {
                    return rewrittenURI;
                }
                
                @Override
                public String getServletPath() {
                    String servletPath = super.getServletPath();
                    if (servletPath.startsWith(adminPath)) {
                        return servletPath.replaceFirst(adminPath, defaultAdminPath);
                    }
                    return servletPath;
                }
                
                @Override
                public String getPathInfo() {
                    String pathInfo = super.getPathInfo();
                    if (pathInfo != null && pathInfo.startsWith(adminPath)) {
                        return pathInfo.replaceFirst(adminPath, defaultAdminPath);
                    }
                    return pathInfo;
                }
            };
            
            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

