package k23cnt1.nqt.project3.nqtConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.NqtJwtService;
import k23cnt1.nqt.project3.nqtService.NqtRateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class NqtOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private NqtJwtService jwtService;

    @Autowired
    private NqtRateLimitService rateLimitService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        try {
            // Check if response is already committed
            if (response.isCommitted()) {
                return;
            }
            
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            
            // Handle missing email
            if (email == null || email.isEmpty()) {
                getRedirectStrategy().sendRedirect(request, response, "/nqtDangNhap?error=oauth2_no_email");
                return;
            }
            
            String name = oauth2User.getAttribute("name");
            if (name == null) {
                name = email;
            }

            // Find or create user
            Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findByNqtEmail(email);
            NqtNguoiDung user;

            if (userOptional.isPresent()) {
                user = userOptional.get();
                
                // Check if account is locked/disabled
                if (user.getNqtStatus() != null && !user.getNqtStatus()) {
                    // Account is disabled/locked - don't allow OAuth login
                    String ipAddress = getClientIpAddress(request);
                    String userAgent = getUserAgent(request);
                    String identifier = user.getNqtTaiKhoan() != null ? user.getNqtTaiKhoan() : user.getNqtEmail();
                    rateLimitService.recordAttempt(identifier, ipAddress, false, "OAuth login blocked - account disabled", userAgent, NqtRateLimitService.ACTION_LOGIN);
                    getRedirectStrategy().sendRedirect(request, response, "/nqtDangNhap?error=account_locked");
                    return;
                }
                
                // Check brute force protection - if account is locked due to too many failed attempts
                String identifier = user.getNqtTaiKhoan() != null ? user.getNqtTaiKhoan() : user.getNqtEmail();
                NqtRateLimitService.RateLimitResult bruteForceCheck = rateLimitService.checkBruteForceProtection(identifier);
                if (bruteForceCheck.isBlocked()) {
                    // Account is locked due to brute force - don't allow OAuth login
                    String ipAddress = getClientIpAddress(request);
                    String userAgent = getUserAgent(request);
                    rateLimitService.recordAttempt(identifier, ipAddress, false, "OAuth login blocked - account locked (brute force)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                    getRedirectStrategy().sendRedirect(request, response, "/nqtDangNhap?error=account_locked&message=" + 
                        java.net.URLEncoder.encode(bruteForceCheck.getMessage(), "UTF-8"));
                    return;
                }
                
                // Update email verification status for existing OAuth users
                // Google has already verified the email, so we can trust it
                if (user.getNqtEmailVerified() == null || !user.getNqtEmailVerified()) {
                    user.setNqtEmailVerified(true);
                    user = nqtNguoiDungRepository.save(user);
                }
            } else {
                // Create new user from OAuth2
                user = new NqtNguoiDung();
                user.setNqtEmail(email);
                user.setNqtHoVaTen(name);
                user.setNqtTaiKhoan(email);
                user.setNqtVaiTro((byte) 0); // Default to customer
                user.setNqtStatus(true);
                user.setNqtCapBac("KhachThuong");
                user.setNqtMatKhau(""); // OAuth users don't need password
                user.setNqtEmailVerified(true); // OAuth emails are already verified by Google
                user = nqtNguoiDungRepository.save(user);
            }

            // Generate JWT token
            String token = jwtService.generateToken(user.getNqtTaiKhoan(), user.getNqtId(), user.getNqtVaiTro());

            // Set JWT in cookie
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400); // 24 hours
            response.addCookie(jwtCookie);

            // Set session for backward compatibility
            request.getSession().setAttribute("nqtCustomerUser", user);
            if (user.getNqtVaiTro() != null && (user.getNqtVaiTro() == 99 || user.getNqtVaiTro() == 1)) {
                request.getSession().setAttribute("nqtAdminUser", user);
                request.getSession().setAttribute("nqtAdminSession", user.getNqtTaiKhoan());
            }

            // Record successful OAuth login attempt
            String ipAddress = getClientIpAddress(request);
            String userAgent = getUserAgent(request);
            String identifier = user.getNqtTaiKhoan() != null ? user.getNqtTaiKhoan() : user.getNqtEmail();
            rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent, NqtRateLimitService.ACTION_LOGIN);

            // Check again if response is committed before redirect
            if (!response.isCommitted()) {
                // Always redirect to customer site (not admin) when logging in from customer site
                // Admin can access admin panel via the "Admin" button in navigation
                getRedirectStrategy().sendRedirect(request, response, "/nqtTrangChu");
            }
        } catch (Exception e) {
            // Log error and redirect to login with error message
            e.printStackTrace();
            if (!response.isCommitted()) {
                getRedirectStrategy().sendRedirect(request, response, "/nqtDangNhap?error=oauth2_error");
            }
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
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
    private String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.length() > 500) {
            userAgent = userAgent.substring(0, 500);
        }
        return userAgent;
    }
}

