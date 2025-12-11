package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.NqtJwtService;
import k23cnt1.nqt.project3.nqtService.Nqt2FAService;
import k23cnt1.nqt.project3.nqtService.NqtRateLimitService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Controller
public class NqtKhachHangController {

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NqtJwtService jwtService;

    @Autowired
    private Nqt2FAService nqt2FAService;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtEmailVerificationService nqtEmailVerificationService;

    @Autowired
    private NqtRateLimitService rateLimitService;

    // Customer Login
    @GetMapping("/nqtDangNhap")
    public String nqtDangNhap(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "message", required = false) String message,
                              HttpSession session, 
                              Model model) {
        // Redirect if already logged in
        if (session.getAttribute("nqtCustomerSession") != null) {
            return "redirect:/nqtTaiKhoan";
        }
        
        // Handle OAuth errors
        if (error != null) {
            if ("oauth2_no_email".equals(error)) {
                model.addAttribute("nqtError", "Không thể lấy thông tin email từ Google. Vui lòng thử lại hoặc đăng nhập bằng tài khoản thường.");
            } else if ("oauth2_failed".equals(error)) {
                model.addAttribute("nqtError", "Đăng nhập bằng Google thất bại. Vui lòng thử lại.");
            } else if ("account_locked".equals(error)) {
                if (message != null && !message.isEmpty()) {
                    try {
                        model.addAttribute("nqtError", java.net.URLDecoder.decode(message, "UTF-8"));
                    } catch (Exception e) {
                        model.addAttribute("nqtError", "Tài khoản đã bị khóa tạm thời do quá nhiều lần đăng nhập sai. Vui lòng thử lại sau.");
                    }
                } else {
                    model.addAttribute("nqtError", "Tài khoản đã bị khóa tạm thời do quá nhiều lần đăng nhập sai. Vui lòng thử lại sau.");
                }
            }
        }
        
        // Set default value for 2FA section
        model.addAttribute("nqtShow2FA", false);
        return "nqtCustomer/nqtAuth/nqtLogin";
    }

    @PostMapping("/nqtDangNhap")
    public String nqtDangNhapSubmit(@RequestParam(value = "nqtTaiKhoan", required = false) String nqtTaiKhoan,
            @RequestParam(value = "nqtMatKhau", required = false) String nqtMatKhau,
            @RequestParam(value = "nqt2faCode", required = false) String nqt2faCode,
            HttpServletRequest request,
            HttpSession session,
            HttpServletResponse response,
            Model model) {
        
        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        
        // Check if this is a 2FA verification (user already verified password)
        Integer pending2FAUserId = (Integer) session.getAttribute("nqtPending2FAUserId");
        if (pending2FAUserId != null && nqt2faCode != null && !nqt2faCode.isEmpty()) {
            // This is 2FA verification step - NO rate limiting here, password already verified
            Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(pending2FAUserId);
            if (userOptional.isPresent()) {
                NqtNguoiDung nqtNguoiDung = userOptional.get();
                String identifier = nqtNguoiDung.getNqtTaiKhoan() != null ? nqtNguoiDung.getNqtTaiKhoan() : nqtNguoiDung.getNqtEmail();
                
                String secret = nqtNguoiDung.getNqt2faSecret();
                if (secret != null && nqt2FAService.verifyCode(secret, nqt2faCode)) {
                    // 2FA verified, complete login
                    rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent, NqtRateLimitService.ACTION_LOGIN);
                    return completeLogin(nqtNguoiDung, session, response);
                } else {
                    rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent, NqtRateLimitService.ACTION_2FA_VERIFICATION);
                    model.addAttribute("nqtError", "Mã xác thực 2FA không đúng!");
                    model.addAttribute("nqtShow2FA", true);
                    model.addAttribute("nqtTaiKhoan", nqtTaiKhoan != null ? nqtTaiKhoan : nqtNguoiDung.getNqtTaiKhoan());
                    return "nqtCustomer/nqtAuth/nqtLogin";
                }
            } else {
                // User not found, clear session and show error
                session.removeAttribute("nqtPending2FAUserId");
                model.addAttribute("nqtError", "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.");
                model.addAttribute("nqtShow2FA", false);
                return "nqtCustomer/nqtAuth/nqtLogin";
            }
        }
        
        // Normal login flow (username + password)
        if (nqtTaiKhoan == null || nqtMatKhau == null) {
            model.addAttribute("nqtError", "Vui lòng nhập đầy đủ thông tin!");
            model.addAttribute("nqtShow2FA", false);
            return "nqtCustomer/nqtAuth/nqtLogin";
        }
        
        // Clean and normalize input
        String cleanedUsername = nqtTaiKhoan.trim()
                .replaceAll(",$", "") // Remove trailing comma
                .replaceAll("^,", "") // Remove leading comma
                .replaceAll("\\s+", " "); // Normalize whitespace
        
        // Debug logging
        System.out.println("🔍 Searching for user (cleaned): [" + cleanedUsername + "]");
        
        // Try exact match first - Find user BEFORE checking rate limit
        Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findByNqtTaiKhoanOrNqtEmail(cleanedUsername,
                cleanedUsername);
        
        // If not found, try case-insensitive search by username
        if (nqtNguoiDungOptional.isEmpty() && !cleanedUsername.isEmpty()) {
            Optional<NqtNguoiDung> byUsername = nqtNguoiDungRepository.findByNqtTaiKhoan(cleanedUsername);
            if (byUsername.isEmpty()) {
                // Try to find all users and match case-insensitively
                final String searchTerm = cleanedUsername; // Final for lambda
                java.util.List<NqtNguoiDung> allUsers = nqtNguoiDungRepository.findAll();
                nqtNguoiDungOptional = allUsers.stream()
                    .filter(u -> (u.getNqtTaiKhoan() != null && u.getNqtTaiKhoan().equalsIgnoreCase(searchTerm)) ||
                                 (u.getNqtEmail() != null && u.getNqtEmail().equalsIgnoreCase(searchTerm)))
                    .findFirst();
            } else {
                nqtNguoiDungOptional = byUsername;
            }
        }
        
        System.out.println("🔍 User found: " + nqtNguoiDungOptional.isPresent());
        if (nqtNguoiDungOptional.isPresent()) {
            System.out.println("🔍 Found user: " + nqtNguoiDungOptional.get().getNqtTaiKhoan() + " / " + nqtNguoiDungOptional.get().getNqtEmail());
        }
        
        // Update nqtTaiKhoan for later use
        nqtTaiKhoan = cleanedUsername;

        if (nqtNguoiDungOptional.isPresent()) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungOptional.get();
            String identifier = nqtNguoiDung.getNqtTaiKhoan() != null ? nqtNguoiDung.getNqtTaiKhoan() : nqtNguoiDung.getNqtEmail();

            // Check if email is verified
            if (nqtNguoiDung.getNqtEmailVerified() == null || !nqtNguoiDung.getNqtEmailVerified()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Email not verified", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtEmailNotVerified", true);
                model.addAttribute("nqtEmailNotVerifiedEmail", nqtNguoiDung.getNqtEmail());
                return "nqtCustomer/nqtAuth/nqtLogin";
            }

            // Check if account is active
            if (nqtNguoiDung.getNqtStatus() == null || !nqtNguoiDung.getNqtStatus()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Account disabled", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", "Tài khoản đã bị khóa hoặc chưa kích hoạt!");
                return "nqtCustomer/nqtAuth/nqtLogin";
            }

            // Check if user is OAuth2 user (no password)
            if (nqtNguoiDung.getNqtMatKhau() == null || nqtNguoiDung.getNqtMatKhau().isEmpty()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "OAuth2 account", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", "Tài khoản này được đăng ký qua Google/Facebook. Vui lòng sử dụng nút đăng nhập với Google/Facebook.");
                model.addAttribute("nqtShow2FA", false);
                return "nqtCustomer/nqtAuth/nqtLogin";
            }

            // Check rate limiting and brute force protection AFTER we know user exists
            NqtRateLimitService.RateLimitResult identifierRateLimit = rateLimitService.checkRateLimitByIdentifier(identifier);
            if (identifierRateLimit.isBlocked()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (identifier)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", identifierRateLimit.getMessage());
                model.addAttribute("nqtShow2FA", false);
                return "nqtCustomer/nqtAuth/nqtLogin";
            }
            
            // Check brute force protection
            NqtRateLimitService.RateLimitResult bruteForceCheck = rateLimitService.checkBruteForceProtection(identifier);
            if (bruteForceCheck.isBlocked()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Account locked (brute force)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", bruteForceCheck.getMessage());
                model.addAttribute("nqtShow2FA", false);
                return "nqtCustomer/nqtAuth/nqtLogin";
            }
            
            // Check IP rate limiting
            NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddress(ipAddress);
            if (ipRateLimit.isBlocked()) {
                rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (IP)", userAgent, NqtRateLimitService.ACTION_LOGIN);
                model.addAttribute("nqtError", ipRateLimit.getMessage());
                model.addAttribute("nqtShow2FA", false);
                return "nqtCustomer/nqtAuth/nqtLogin";
            }

            // Check password
            boolean isMatch = passwordEncoder.matches(nqtMatKhau, nqtNguoiDung.getNqtMatKhau());

            // Support plain text passwords (lazy migration)
            if (!isMatch && nqtNguoiDung.getNqtMatKhau().equals(nqtMatKhau)) {
                isMatch = true;
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
                            return completeLogin(nqtNguoiDung, session, response);
                        } else {
                            rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent, NqtRateLimitService.ACTION_2FA_VERIFICATION);
                            model.addAttribute("nqtError", "Mã xác thực 2FA không đúng!");
                            model.addAttribute("nqtShow2FA", true);
                            model.addAttribute("nqtTaiKhoan", nqtTaiKhoan);
                            return "nqtCustomer/nqtAuth/nqtLogin";
                        }
                    } else {
                        // Password correct but need 2FA code
                        // Store user ID in session temporarily for 2FA verification
                        session.setAttribute("nqtPending2FAUserId", nqtNguoiDung.getNqtId());
                        model.addAttribute("nqtShow2FA", true);
                        model.addAttribute("nqtTaiKhoan", nqtTaiKhoan);
                        return "nqtCustomer/nqtAuth/nqtLogin";
                    }
                } else {
                    // No 2FA, complete login directly
                    return completeLogin(nqtNguoiDung, session, response);
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
                return "nqtCustomer/nqtAuth/nqtLogin";
            }
            
            rateLimitService.recordAttempt(cleanedUsername, ipAddress, false, "User not found", userAgent, NqtRateLimitService.ACTION_LOGIN);
            model.addAttribute("nqtError", "Tài khoản hoặc Email không tồn tại!");
        }

        return "nqtCustomer/nqtAuth/nqtLogin";
    }

    private String completeLogin(NqtNguoiDung nqtNguoiDung, HttpSession session, HttpServletResponse response) {
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
        
        // Set customer session for backward compatibility
        session.setAttribute("nqtCustomerSession", nqtNguoiDung.getNqtTaiKhoan());
        session.setAttribute("nqtCustomerUser", nqtNguoiDung);
        
        // If user is admin/employee, also set admin session
        if (nqtNguoiDung.getNqtVaiTro() != null && (nqtNguoiDung.getNqtVaiTro() == 99 || nqtNguoiDung.getNqtVaiTro() == 1)) {
            session.setAttribute("nqtAdminUser", nqtNguoiDung);
            session.setAttribute("nqtAdminSession", nqtNguoiDung.getNqtTaiKhoan());
        }
        
        return "redirect:/nqtTrangChu";
    }

    // Customer Registration
    @GetMapping("/nqtDangKy")
    public String nqtDangKy(HttpSession session, Model model) {
        // Redirect if already logged in
        if (session.getAttribute("nqtCustomerSession") != null) {
            return "redirect:/nqtTaiKhoan";
        }
        return "nqtCustomer/nqtAuth/nqtRegister";
    }

    @PostMapping("/nqtDangKy")
    public String nqtDangKySubmit(@RequestParam("nqtHoVaTen") String nqtHoVaTen,
            @RequestParam("nqtTaiKhoan") String nqtTaiKhoan,
            @RequestParam("nqtEmail") String nqtEmail,
            @RequestParam("nqtMatKhau") String nqtMatKhau,
            @RequestParam("nqtMatKhauXacNhan") String nqtMatKhauXacNhan,
            @RequestParam(value = "nqtSoDienThoai", required = false) String nqtSoDienThoai,
            @RequestParam(value = "nqtDiaChi", required = false) String nqtDiaChi,
            HttpServletRequest request,
            Model model) {
        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        
        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_REGISTER,
            "Quá nhiều lần đăng ký từ địa chỉ IP này. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(nqtEmail, ipAddress, false, "Rate limit exceeded (IP)", userAgent,
                                          NqtRateLimitService.ACTION_REGISTER);
            model.addAttribute("nqtError", ipRateLimit.getMessage());
            return "nqtCustomer/nqtAuth/nqtRegister";
        }
        
        // Check rate limiting for email
        NqtRateLimitService.RateLimitResult emailRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            nqtEmail, NqtRateLimitService.ACTION_REGISTER,
            "Quá nhiều lần đăng ký với email này. Vui lòng đợi một chút trước khi thử lại.");
        if (emailRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(nqtEmail, ipAddress, false, "Rate limit exceeded (email)", userAgent,
                                          NqtRateLimitService.ACTION_REGISTER);
            model.addAttribute("nqtError", emailRateLimit.getMessage());
            return "nqtCustomer/nqtAuth/nqtRegister";
        }
        
        // Validate passwords match
        if (!nqtMatKhau.equals(nqtMatKhauXacNhan)) {
            rateLimitService.recordAttempt(nqtEmail, ipAddress, false, "Password mismatch", userAgent,
                                          NqtRateLimitService.ACTION_REGISTER);
            model.addAttribute("nqtError", "Mật khẩu xác nhận không khớp!");
            return "nqtCustomer/nqtAuth/nqtRegister";
        }

        // Check if username already exists
        if (nqtNguoiDungRepository.findByNqtTaiKhoan(nqtTaiKhoan).isPresent()) {
            rateLimitService.recordAttempt(nqtEmail, ipAddress, false, "Username already exists", userAgent,
                                          NqtRateLimitService.ACTION_REGISTER);
            model.addAttribute("nqtError", "Tài khoản đã tồn tại!");
            return "nqtCustomer/nqtAuth/nqtRegister";
        }

        // Check if email already exists
        if (nqtNguoiDungRepository.findByNqtEmail(nqtEmail).isPresent()) {
            rateLimitService.recordAttempt(nqtEmail, ipAddress, false, "Email already exists", userAgent,
                                          NqtRateLimitService.ACTION_REGISTER);
            model.addAttribute("nqtError", "Email đã được sử dụng!");
            return "nqtCustomer/nqtAuth/nqtRegister";
        }

        // Create new customer
        NqtNguoiDung nqtNguoiDung = new NqtNguoiDung();
        nqtNguoiDung.setNqtHoVaTen(nqtHoVaTen);
        nqtNguoiDung.setNqtTaiKhoan(nqtTaiKhoan);
        nqtNguoiDung.setNqtEmail(nqtEmail);
        nqtNguoiDung.setNqtMatKhau(passwordEncoder.encode(nqtMatKhau));
        nqtNguoiDung.setNqtSoDienThoai(nqtSoDienThoai);
        nqtNguoiDung.setNqtDiaChi(nqtDiaChi);
        nqtNguoiDung.setNqtVaiTro((byte) 0); // Customer role
        nqtNguoiDung.setNqtStatus(false); // Inactive until email verified
        nqtNguoiDung.setNqtEmailVerified(false); // Email not verified yet
        nqtNguoiDung.setNqtCapBac("KhachThuong"); // Default customer level

        nqtNguoiDungRepository.save(nqtNguoiDung);

        // Send verification email
        try {
            nqtEmailVerificationService.sendVerificationEmail(nqtNguoiDung);
            rateLimitService.recordAttempt(nqtEmail, ipAddress, true, null, userAgent,
                                          NqtRateLimitService.ACTION_REGISTER);
            model.addAttribute("nqtSuccess", "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
        } catch (Exception e) {
            // If email sending fails, still allow registration but warn user
            rateLimitService.recordAttempt(nqtEmail, ipAddress, false, "Email send failed: " + e.getMessage(), userAgent,
                                          NqtRateLimitService.ACTION_REGISTER);
            model.addAttribute("nqtWarning", "Đăng ký thành công nhưng không thể gửi email xác thực. Vui lòng liên hệ admin để kích hoạt tài khoản.");
        }
        
        return "nqtCustomer/nqtAuth/nqtLogin";
    }

    // Customer Logout
    @GetMapping("/nqtDangXuat")
    public String nqtDangXuat(HttpSession session, HttpServletResponse response) {
        // Clear SecurityContext (JWT authentication)
        SecurityContextHolder.clearContext();
        
        // Clear JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete cookie
        response.addCookie(jwtCookie);
        
        // Clear all session attributes (both customer and admin if exists)
        session.removeAttribute("nqtCustomerSession");
        session.removeAttribute("nqtCustomerUser");
        session.removeAttribute("nqtAdminSession");
        session.removeAttribute("nqtAdminUser");
        session.removeAttribute("nqtPending2FAUserId");
        
        return "redirect:/nqtTrangChu";
    }

    // Customer Profile/Dashboard
    @GetMapping("/nqtTaiKhoan")
    public String nqtTaiKhoan(HttpSession session, Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        // Check for flash messages from redirect
        String passwordChangeSuccess = (String) session.getAttribute("nqtPasswordChangeSuccess");
        if (passwordChangeSuccess != null) {
            model.addAttribute("nqtSuccess", passwordChangeSuccess);
            session.removeAttribute("nqtPasswordChangeSuccess");
        }
        
        String profileUpdateSuccess = (String) session.getAttribute("nqtProfileUpdateSuccess");
        if (profileUpdateSuccess != null) {
            model.addAttribute("nqtSuccess", profileUpdateSuccess);
            session.removeAttribute("nqtProfileUpdateSuccess");
        }

        // Refresh user data from database
        Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findById(nqtCustomerUser.getNqtId());
        if (nqtNguoiDungOptional.isPresent()) {
            NqtNguoiDung freshUser = nqtNguoiDungOptional.get();
            // Update session with fresh data
            session.setAttribute("nqtCustomerUser", freshUser);
            model.addAttribute("nqtCustomerUser", freshUser);
        }

        return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
    }

    // Update Profile
    @PostMapping("/nqtTaiKhoan/cap-nhat")
    @Transactional
    public String nqtCapNhatTaiKhoan(@RequestParam("nqtHoVaTen") String nqtHoVaTen,
            @RequestParam("nqtEmail") String nqtEmail,
            @RequestParam(value = "nqtSoDienThoai", required = false) String nqtSoDienThoai,
            @RequestParam(value = "nqtDiaChi", required = false) String nqtDiaChi,
            @RequestParam(value = "nqtMatKhauCu", required = false) String nqtMatKhauCu,
            @RequestParam(value = "nqtMatKhauMoi", required = false) String nqtMatKhauMoi,
            @RequestParam(value = "nqtMatKhauMoiXacNhan", required = false) String nqtMatKhauMoiXacNhan,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findById(nqtCustomerUser.getNqtId());
        if (nqtNguoiDungOptional.isPresent()) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungOptional.get();

            // Check if email is already used by another user
            Optional<NqtNguoiDung> emailCheck = nqtNguoiDungRepository.findByNqtEmail(nqtEmail);
            if (emailCheck.isPresent() && !emailCheck.get().getNqtId().equals(nqtNguoiDung.getNqtId())) {
                model.addAttribute("nqtError", "Email đã được sử dụng bởi tài khoản khác!");
                model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
            }

            // Handle password change if provided
            boolean passwordChangeRequested = (nqtMatKhauCu != null && !nqtMatKhauCu.trim().isEmpty()) ||
                                             (nqtMatKhauMoi != null && !nqtMatKhauMoi.trim().isEmpty()) ||
                                             (nqtMatKhauMoiXacNhan != null && !nqtMatKhauMoiXacNhan.trim().isEmpty());
            
            // Check if user is OAuth user (no password set)
            boolean isOAuthUser = nqtNguoiDung.getNqtMatKhau() == null || nqtNguoiDung.getNqtMatKhau().trim().isEmpty();
            
            if (passwordChangeRequested) {
                if (nqtMatKhauMoi == null || nqtMatKhauMoi.trim().isEmpty()) {
                    model.addAttribute("nqtError", "Vui lòng nhập mật khẩu mới!");
                    model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                    return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                }

                // Trim passwords
                nqtMatKhauMoi = nqtMatKhauMoi.trim();
                nqtMatKhauMoiXacNhan = nqtMatKhauMoiXacNhan != null ? nqtMatKhauMoiXacNhan.trim() : "";

                // For OAuth users, skip old password verification
                // For regular users, require old password
                if (!isOAuthUser) {
                    if (nqtMatKhauCu == null || nqtMatKhauCu.trim().isEmpty()) {
                        model.addAttribute("nqtError", "Vui lòng nhập mật khẩu cũ!");
                        model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                        return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                    }
                    
                    // Trim old password
                    nqtMatKhauCu = nqtMatKhauCu.trim();
                    
                    // Verify old password
                    boolean isMatch = passwordEncoder.matches(nqtMatKhauCu, nqtNguoiDung.getNqtMatKhau());
                    
                    // Support plain text passwords (lazy migration)
                    if (!isMatch && nqtNguoiDung.getNqtMatKhau() != null && nqtNguoiDung.getNqtMatKhau().equals(nqtMatKhauCu)) {
                        isMatch = true;
                    }
                    
                    if (!isMatch) {
                        model.addAttribute("nqtError", "Mật khẩu cũ không đúng!");
                        model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                        return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                    }
                }

                // Verify new passwords match
                if (!nqtMatKhauMoi.equals(nqtMatKhauMoiXacNhan)) {
                    model.addAttribute("nqtError", "Mật khẩu mới xác nhận không khớp!");
                    model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                    return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                }
                
                // Check minimum password length
                if (nqtMatKhauMoi.length() < 6) {
                    model.addAttribute("nqtError", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                    model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                    return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                }
                
                // Check if new password is different from current password (only for non-OAuth users)
                if (!isOAuthUser) {
                    boolean newPasswordMatchesCurrent = passwordEncoder.matches(nqtMatKhauMoi, nqtNguoiDung.getNqtMatKhau());
                    if (!newPasswordMatchesCurrent && nqtNguoiDung.getNqtMatKhau() != null && nqtNguoiDung.getNqtMatKhau().equals(nqtMatKhauMoi)) {
                        newPasswordMatchesCurrent = true;
                    }
                    if (newPasswordMatchesCurrent) {
                        model.addAttribute("nqtError", "Mật khẩu mới phải khác với mật khẩu hiện tại!");
                        model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                        return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                    }
                }

                // Update password directly on entity (more reliable than @Modifying query)
                String encodedPassword = passwordEncoder.encode(nqtMatKhauMoi);
                nqtNguoiDung.setNqtMatKhau(encodedPassword);
                System.out.println("🔐 Setting new password hash on entity");
                // Save immediately to ensure password is updated
                nqtNguoiDungRepository.save(nqtNguoiDung);
                nqtNguoiDungRepository.flush();
                System.out.println("🔐 Password saved and flushed");
                
                // Verify password was saved correctly
                Optional<NqtNguoiDung> verifyPassword = nqtNguoiDungRepository.findById(nqtNguoiDung.getNqtId());
                if (verifyPassword.isPresent()) {
                    boolean passwordMatches = passwordEncoder.matches(nqtMatKhauMoi, verifyPassword.get().getNqtMatKhau());
                    System.out.println("🔐 Password verification after save: " + (passwordMatches ? "SUCCESS" : "FAILED"));
                    if (!passwordMatches) {
                        model.addAttribute("nqtError", "Lỗi: Mật khẩu không được lưu đúng. Vui lòng thử lại!");
                        model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                        return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                    }
                    // Reload entity to get fresh password
                    nqtNguoiDung = verifyPassword.get();
                }
            }

            // Update profile information
            nqtNguoiDung.setNqtHoVaTen(nqtHoVaTen);
            nqtNguoiDung.setNqtEmail(nqtEmail);
            nqtNguoiDung.setNqtSoDienThoai(nqtSoDienThoai);
            nqtNguoiDung.setNqtDiaChi(nqtDiaChi);

            // Save profile changes (password already updated and verified above)
            NqtNguoiDung savedUser = nqtNguoiDungRepository.save(nqtNguoiDung);
            nqtNguoiDungRepository.flush();
            
            // Reload from database to get fresh data (including updated password)
            Optional<NqtNguoiDung> freshUserOptional = nqtNguoiDungRepository.findById(savedUser.getNqtId());
            if (freshUserOptional.isPresent()) {
                NqtNguoiDung freshUser = freshUserOptional.get();
                session.setAttribute("nqtCustomerUser", freshUser);
                if (freshUser.getNqtVaiTro() != null && (freshUser.getNqtVaiTro() == 99 || freshUser.getNqtVaiTro() == 1)) {
                    session.setAttribute("nqtAdminUser", freshUser);
                }
                model.addAttribute("nqtCustomerUser", freshUser);
            } else {
                model.addAttribute("nqtCustomerUser", nqtNguoiDung);
            }

            String successMessage = passwordChangeRequested ? 
                "Cập nhật thông tin và đổi mật khẩu thành công!" : 
                "Cập nhật thông tin thành công!";
            session.setAttribute("nqtProfileUpdateSuccess", successMessage);
            return "redirect:/nqtTaiKhoan";
        }

        return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
    }

    // Change Password
    @PostMapping("/nqtTaiKhoan/doi-mat-khau")
    @Transactional
    public String nqtDoiMatKhau(@RequestParam(value = "nqtMatKhauCu", required = false) String nqtMatKhauCu,
            @RequestParam(value = "nqtMatKhauMoi", required = false) String nqtMatKhauMoi,
            @RequestParam(value = "nqtMatKhauMoiXacNhan", required = false) String nqtMatKhauMoiXacNhan,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        // If all password fields are empty, skip password change
        if ((nqtMatKhauCu == null || nqtMatKhauCu.trim().isEmpty()) &&
            (nqtMatKhauMoi == null || nqtMatKhauMoi.trim().isEmpty()) &&
            (nqtMatKhauMoiXacNhan == null || nqtMatKhauMoiXacNhan.trim().isEmpty())) {
            // No password change requested, just return success
            Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findById(nqtCustomerUser.getNqtId());
            if (nqtNguoiDungOptional.isPresent()) {
                model.addAttribute("nqtCustomerUser", nqtNguoiDungOptional.get());
            }
            return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
        }

        if (nqtMatKhauMoi == null || nqtMatKhauMoi.trim().isEmpty()) {
            model.addAttribute("nqtError", "Vui lòng nhập mật khẩu mới!");
            Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findById(nqtCustomerUser.getNqtId());
            if (nqtNguoiDungOptional.isPresent()) {
                model.addAttribute("nqtCustomerUser", nqtNguoiDungOptional.get());
            }
            return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
        }

        // Reload user from database to get fresh data
        Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findById(nqtCustomerUser.getNqtId());
        if (nqtNguoiDungOptional.isPresent()) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungOptional.get();
            
            // Check if user is OAuth user (no password set)
            boolean isOAuthUser = nqtNguoiDung.getNqtMatKhau() == null || nqtNguoiDung.getNqtMatKhau().trim().isEmpty();
            
            // For OAuth users, skip old password verification
            // For regular users, require old password
            if (!isOAuthUser) {
                if (nqtMatKhauCu == null || nqtMatKhauCu.trim().isEmpty()) {
                    model.addAttribute("nqtError", "Vui lòng nhập mật khẩu cũ!");
                    model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                    return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                }
                
                // Trim old password input
                nqtMatKhauCu = nqtMatKhauCu != null ? nqtMatKhauCu.trim() : "";

                // Verify old password
                boolean isMatch = passwordEncoder.matches(nqtMatKhauCu, nqtNguoiDung.getNqtMatKhau());
                
                // Support plain text passwords (lazy migration)
                if (!isMatch && nqtNguoiDung.getNqtMatKhau() != null && nqtNguoiDung.getNqtMatKhau().equals(nqtMatKhauCu)) {
                    isMatch = true;
                }
                
                if (!isMatch) {
                    System.out.println("🔐 Old password verification FAILED");
                    System.out.println("🔐 Input password: [" + nqtMatKhauCu + "]");
                    System.out.println("🔐 Stored password hash: " + (nqtNguoiDung.getNqtMatKhau() != null ? nqtNguoiDung.getNqtMatKhau().substring(0, Math.min(20, nqtNguoiDung.getNqtMatKhau().length())) + "..." : "NULL"));
                    model.addAttribute("nqtError", "Mật khẩu cũ không đúng!");
                    model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                    return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                }
            }

            // Trim password to remove any leading/trailing whitespace
            nqtMatKhauMoi = nqtMatKhauMoi != null ? nqtMatKhauMoi.trim() : "";
            nqtMatKhauMoiXacNhan = nqtMatKhauMoiXacNhan != null ? nqtMatKhauMoiXacNhan.trim() : "";

            // Verify new passwords match
            if (!nqtMatKhauMoi.equals(nqtMatKhauMoiXacNhan)) {
                model.addAttribute("nqtError", "Mật khẩu mới xác nhận không khớp!");
                model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
            }
            
            // Check minimum password length
            if (nqtMatKhauMoi.length() < 6) {
                model.addAttribute("nqtError", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
            }
            
            // Check if new password is different from current password (only for non-OAuth users)
            if (!isOAuthUser) {
                boolean newPasswordMatchesCurrent = passwordEncoder.matches(nqtMatKhauMoi, nqtNguoiDung.getNqtMatKhau());
                // Also check plain text comparison for backward compatibility
                if (!newPasswordMatchesCurrent && nqtNguoiDung.getNqtMatKhau() != null && nqtNguoiDung.getNqtMatKhau().equals(nqtMatKhauMoi)) {
                    newPasswordMatchesCurrent = true;
                }
                if (newPasswordMatchesCurrent) {
                    model.addAttribute("nqtError", "Mật khẩu mới phải khác với mật khẩu hiện tại!");
                    model.addAttribute("nqtCustomerUser", nqtNguoiDung);
                    return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                }
            }

            // Encode new password
            String encodedPassword = passwordEncoder.encode(nqtMatKhauMoi);
            System.out.println("🔐 Encoding new password: [" + nqtMatKhauMoi + "]");
            System.out.println("🔐 Encoded password hash: " + encodedPassword.substring(0, Math.min(30, encodedPassword.length())) + "...");
            
            // Update password directly on entity (more reliable)
            nqtNguoiDung.setNqtMatKhau(encodedPassword);
            NqtNguoiDung savedUser = nqtNguoiDungRepository.save(nqtNguoiDung);
            nqtNguoiDungRepository.flush();
            System.out.println("🔐 Password saved and flushed");
            
            // Clear any cache and reload from database to verify
            Optional<NqtNguoiDung> verifyUser = nqtNguoiDungRepository.findById(savedUser.getNqtId());
            if (verifyUser.isPresent()) {
                NqtNguoiDung freshUser = verifyUser.get();
                
                // Test if new password works
                boolean testMatch = passwordEncoder.matches(nqtMatKhauMoi, freshUser.getNqtMatKhau());
                System.out.println("🔐 Password change verification: " + (testMatch ? "SUCCESS" : "FAILED"));
                System.out.println("🔐 New password: [" + nqtMatKhauMoi + "]");
                System.out.println("🔐 Stored password hash: " + (freshUser.getNqtMatKhau() != null ? freshUser.getNqtMatKhau().substring(0, Math.min(30, freshUser.getNqtMatKhau().length())) + "..." : "NULL"));
                
                if (!testMatch) {
                    System.out.println("🔐 ERROR: Password verification failed after save!");
                    model.addAttribute("nqtError", "Lỗi: Mật khẩu không được lưu đúng. Vui lòng thử lại!");
                    model.addAttribute("nqtCustomerUser", freshUser);
                    return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
                }
                
                // Update session with fresh user data
                session.setAttribute("nqtCustomerUser", freshUser);
                if (freshUser.getNqtVaiTro() != null && (freshUser.getNqtVaiTro() == 99 || freshUser.getNqtVaiTro() == 1)) {
                    session.setAttribute("nqtAdminUser", freshUser);
                }
                
                System.out.println("🔐 Password changed successfully!");
                
                // Use Post-Redirect-Get pattern to prevent form resubmission
                session.setAttribute("nqtPasswordChangeSuccess", "Đổi mật khẩu thành công!");
                return "redirect:/nqtTaiKhoan";
            } else {
                model.addAttribute("nqtError", "Lỗi: Không thể xác minh mật khẩu. Vui lòng thử lại!");
                model.addAttribute("nqtCustomerUser", nqtNguoiDung);
            }
        }

        return "nqtCustomer/nqtTaiKhoan/nqtDashboard";
    }
}
