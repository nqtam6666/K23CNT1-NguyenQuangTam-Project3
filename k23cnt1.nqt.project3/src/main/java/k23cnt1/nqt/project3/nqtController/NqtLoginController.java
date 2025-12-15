package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.NqtJwtService;
import k23cnt1.nqt.project3.nqtService.Nqt2FAService;
import k23cnt1.nqt.project3.nqtService.NqtRateLimitService;
import k23cnt1.nqt.project3.nqtService.NqtAdminPathService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class NqtLoginController {

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NqtJwtService jwtService;

    @Autowired
    private Nqt2FAService nqt2FAService;

    @Autowired
    private NqtRateLimitService rateLimitService;

    @Autowired
    private NqtAdminPathService adminPathService;

    @GetMapping({"/admin/login", "/{path}/login"})
    public String nqtLogin(@PathVariable(required = false) String path,
                          @RequestParam(value = "error", required = false) String error, 
                          Model model) {
        // Validate that the path matches the current admin path (if path variable is used)
        if (path != null) {
            String currentAdminPath = adminPathService.getAdminPath();
            if (!path.equals(currentAdminPath) && !path.equals("admin")) {
                // Path doesn't match admin path, redirect to correct login
                String adminLoginPath = adminPathService.getAdminLoginPath();
                return "redirect:" + adminLoginPath;
            }
        }
        
        if ("noPermission".equals(error)) {
            model.addAttribute("nqtError", "Bạn không có quyền truy cập Admin! Chỉ nhân viên và admin mới được phép.");
        }
        // Set default value for 2FA section
        model.addAttribute("nqtShow2FA", false);
        return "admin/login";
    }

    @PostMapping({"/admin/login", "/{path}/login"})
    public String nqtLoginSubmit(@PathVariable(required = false) String path,
            @RequestParam(value = "nqtTaiKhoan", required = false) String nqtTaiKhoan,
            @RequestParam(value = "nqtMatKhau", required = false) String nqtMatKhau,
            @RequestParam(value = "nqt2faCode", required = false) String nqt2faCode,
            HttpServletRequest request,
            HttpSession session,
            HttpServletResponse response,
            Model model) {
        
        // Validate that the path matches the current admin path (if path variable is used)
        if (path != null) {
            String currentAdminPath = adminPathService.getAdminPath();
            if (!path.equals(currentAdminPath) && !path.equals("admin")) {
                // Path doesn't match admin path, redirect to correct login
                String adminLoginPath = adminPathService.getAdminLoginPath();
                return "redirect:" + adminLoginPath;
            }
        }
        
        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        
        // Check if this is a 2FA verification (user already verified password)
        Integer pending2FAUserId = (Integer) session.getAttribute("nqtPending2FAUserId");
        if (pending2FAUserId != null && nqt2faCode != null && !nqt2faCode.isEmpty()) {
            // This is 2FA verification step
            Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(pending2FAUserId);
            if (userOptional.isPresent()) {
                NqtNguoiDung nqtNguoiDung = userOptional.get();
                String identifier = nqtNguoiDung.getNqtTaiKhoan() != null ? nqtNguoiDung.getNqtTaiKhoan() : nqtNguoiDung.getNqtEmail();
                
                // NO rate limiting for 2FA verification - password already verified
                // Check role again
                if (nqtNguoiDung.getNqtVaiTro() == null || 
                    (nqtNguoiDung.getNqtVaiTro() != 99 && nqtNguoiDung.getNqtVaiTro() != 1)) {
                    session.removeAttribute("nqtPending2FAUserId");
                    rateLimitService.recordLoginAttempt(identifier, ipAddress, false, "No permission", userAgent);
                    model.addAttribute("nqtError", "Bạn không có quyền truy cập Admin!");
                    model.addAttribute("nqtShow2FA", false);
                    return "admin/login";
                }
                
                String secret = nqtNguoiDung.getNqt2faSecret();
                if (secret != null && nqt2FAService.verifyCode(secret, nqt2faCode)) {
                    // 2FA verified, complete login
                    rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent, NqtRateLimitService.ACTION_LOGIN);
                    return completeAdminLogin(nqtNguoiDung, session, response);
                } else {
                    rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent, NqtRateLimitService.ACTION_2FA_VERIFICATION);
                    model.addAttribute("nqtError", "Mã xác thực 2FA không đúng!");
                    model.addAttribute("nqtShow2FA", true);
                    model.addAttribute("nqtTaiKhoan", nqtTaiKhoan != null ? nqtTaiKhoan : nqtNguoiDung.getNqtTaiKhoan());
                    return "admin/login";
                }
            } else {
                // User not found, clear session and show error
                session.removeAttribute("nqtPending2FAUserId");
                model.addAttribute("nqtError", "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.");
                model.addAttribute("nqtShow2FA", false);
                return "admin/login";
            }
        }
        
        // Normal login flow (username + password)
        if (nqtTaiKhoan == null || nqtMatKhau == null) {
            model.addAttribute("nqtError", "Vui lòng nhập đầy đủ thông tin!");
            model.addAttribute("nqtShow2FA", false);
            return "admin/login";
        }
        
        // Clean and normalize input
        String cleanedUsername = nqtTaiKhoan.trim()
                .replaceAll(",$", "") // Remove trailing comma
                .replaceAll("^,", "") // Remove leading comma
                .replaceAll("\\s+", " "); // Normalize whitespace
        
        // Tìm kiếm theo Tài khoản hoặc Email TRƯỚC KHI check rate limit
        // Rate limit chỉ áp dụng cho failed attempts, không áp dụng trước khi biết user có tồn tại không
        Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findByNqtTaiKhoanOrNqtEmail(cleanedUsername,
                cleanedUsername);

        if (nqtNguoiDungOptional.isPresent()) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungOptional.get();
            String identifier = nqtNguoiDung.getNqtTaiKhoan() != null ? nqtNguoiDung.getNqtTaiKhoan() : nqtNguoiDung.getNqtEmail();

            // Kiểm tra trạng thái hoạt động
            if (nqtNguoiDung.getNqtStatus() != null && !nqtNguoiDung.getNqtStatus()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Account disabled", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", "Tài khoản đã bị khóa hoặc chưa kích hoạt!");
                return "admin/login";
            }

            // Kiểm tra quyền trước
            if (nqtNguoiDung.getNqtVaiTro() == null || 
                (nqtNguoiDung.getNqtVaiTro() != 99 && nqtNguoiDung.getNqtVaiTro() != 1)) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "No permission", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", "Bạn không có quyền truy cập Admin!");
                return "admin/login";
            }

            // Check rate limiting and brute force protection AFTER we know user exists
            // Only check for this specific user, not for the input username (which might be different)
            NqtRateLimitService.RateLimitResult identifierRateLimit = rateLimitService.checkRateLimitByIdentifier(identifier);
            if (identifierRateLimit.isBlocked()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (identifier)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", identifierRateLimit.getMessage());
                model.addAttribute("nqtShow2FA", false);
                return "admin/login";
            }
            
            // Check brute force protection
            NqtRateLimitService.RateLimitResult bruteForceCheck = rateLimitService.checkBruteForceProtection(identifier);
            if (bruteForceCheck.isBlocked()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Account locked (brute force)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", bruteForceCheck.getMessage());
                model.addAttribute("nqtShow2FA", false);
                return "admin/login";
            }
            
            // Check IP rate limiting (only for failed attempts, but check before password verification)
            NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddress(ipAddress);
            if (ipRateLimit.isBlocked()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (IP)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", ipRateLimit.getMessage());
                model.addAttribute("nqtShow2FA", false);
                return "admin/login";
            }

            // Kiểm tra mật khẩu (ưu tiên BCrypt)
            boolean isMatch = passwordEncoder.matches(nqtMatKhau, nqtNguoiDung.getNqtMatKhau());

            // Nếu không khớp hash, kiểm tra plain text (để hỗ trợ mật khẩu cũ)
            if (!isMatch && nqtNguoiDung.getNqtMatKhau().equals(nqtMatKhau)) {
                isMatch = true;
                // Lazy Migration: Cập nhật mật khẩu sang BCrypt
                nqtNguoiDung.setNqtMatKhau(passwordEncoder.encode(nqtMatKhau));
                nqtNguoiDungRepository.save(nqtNguoiDung);
            }

            if (isMatch) {
                // Password is correct - record successful attempt
                rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent, NqtRateLimitService.ACTION_LOGIN);
                
                // Check if 2FA is enabled
                boolean is2FAEnabled = nqtNguoiDung.getNqt2faEnabled() != null && nqtNguoiDung.getNqt2faEnabled();
                
                if (is2FAEnabled) {
                    // If 2FA code is provided, verify it
                    if (nqt2faCode != null && !nqt2faCode.isEmpty()) {
                        String secret = nqtNguoiDung.getNqt2faSecret();
                        if (secret != null && nqt2FAService.verifyCode(secret, nqt2faCode)) {
                            // 2FA verified, complete login
                            return completeAdminLogin(nqtNguoiDung, session, response);
                        } else {
                            rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent, NqtRateLimitService.ACTION_2FA_VERIFICATION);
                            model.addAttribute("nqtError", "Mã xác thực 2FA không đúng!");
                            model.addAttribute("nqtShow2FA", true);
                            model.addAttribute("nqtTaiKhoan", nqtTaiKhoan);
                            return "admin/login";
                        }
                    } else {
                        // Password correct but need 2FA code
                        session.setAttribute("nqtPending2FAUserId", nqtNguoiDung.getNqtId());
                        model.addAttribute("nqtShow2FA", true);
                        model.addAttribute("nqtTaiKhoan", nqtTaiKhoan);
                        return "admin/login";
                    }
                } else {
                    // No 2FA, complete login directly
                    return completeAdminLogin(nqtNguoiDung, session, response);
                }
            } else {
                // Password is wrong - record failed attempt
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Wrong password", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", "Sai mật khẩu!");
            }
        } else {
            // User not found - check if this identifier has been locked (for non-existent users, we still track attempts)
            // But we should check brute force protection to show lock message if applicable
            NqtRateLimitService.RateLimitResult bruteForceCheck = rateLimitService.checkBruteForceProtection(cleanedUsername);
            if (bruteForceCheck.isBlocked()) {
                rateLimitService.recordAttempt(cleanedUsername, ipAddress, false, "Account locked (brute force)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", bruteForceCheck.getMessage());
                model.addAttribute("nqtShow2FA", false);
                return "admin/login";
            }
            
            // Try to find by exact username first for better error message
            Optional<NqtNguoiDung> byUsername = nqtNguoiDungRepository.findByNqtTaiKhoan(cleanedUsername);
            Optional<NqtNguoiDung> byEmail = nqtNguoiDungRepository.findByNqtEmail(cleanedUsername);
            
            if (byUsername.isPresent() || byEmail.isPresent()) {
                rateLimitService.recordAttempt(cleanedUsername, ipAddress, false, "Wrong password", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", "Sai mật khẩu!");
            } else {
                rateLimitService.recordAttempt(cleanedUsername, ipAddress, false, "User not found", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", "Tài khoản hoặc Email không tồn tại!");
            }
        }

        // Set default values
        model.addAttribute("nqtShow2FA", false);
        return "admin/login";
    }

    private String completeAdminLogin(NqtNguoiDung nqtNguoiDung, HttpSession session, HttpServletResponse response) {
        // Generate JWT token
        String token = jwtService.generateToken(
            nqtNguoiDung.getNqtTaiKhoan(),
            nqtNguoiDung.getNqtId(),
            nqtNguoiDung.getNqtVaiTro()
        );
        
        // Set JWT in cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400); // 24 hours
        response.addCookie(jwtCookie);
        
        // Clear pending 2FA session
        session.removeAttribute("nqtPending2FAUserId");
        
        // Set session for backward compatibility
        session.setAttribute("nqtAdminSession", nqtNguoiDung.getNqtTaiKhoan());
        session.setAttribute("nqtAdminUser", nqtNguoiDung);
        // Use dynamic admin path for redirect
        String adminPath = adminPathService.getAdminPathWithSlash();
        return "redirect:" + adminPath;
    }

    @GetMapping({"/admin/logout", "/{path}/logout"})
    public String nqtLogout(@PathVariable(required = false) String path, 
                           HttpServletRequest request, 
                           HttpSession session, 
                           HttpServletResponse response) {
        // Validate that the path matches the current admin path (if path variable is used)
        if (path != null) {
            String currentAdminPath = adminPathService.getAdminPath();
            if (!path.equals(currentAdminPath) && !path.equals("admin")) {
                // Path doesn't match admin path, redirect to correct login
                String adminLoginPath = adminPathService.getAdminLoginPath();
                return "redirect:" + adminLoginPath;
            }
        }
        
        // Clear SecurityContext (JWT authentication)
        SecurityContextHolder.clearContext();
        
        // Clear all session attributes explicitly
        if (session != null) {
            session.removeAttribute("nqtAdminSession");
            session.removeAttribute("nqtAdminUser");
            session.removeAttribute("nqtPending2FAUserId");
            session.removeAttribute("nqtCustomerSession");
            session.removeAttribute("nqtCustomerUser");
            
            // Invalidate session (clears all session attributes)
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // Session already invalidated, ignore
            }
        }
        
        // Clear JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete cookie
        response.addCookie(jwtCookie);
        
        // Use dynamic admin path for redirect
        String adminLoginPath = adminPathService.getAdminLoginPath();
        return "redirect:" + adminLoginPath;
    }
}
