package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.NqtJwtService;
import k23cnt1.nqt.project3.nqtService.Nqt2FAService;
import jakarta.servlet.http.Cookie;
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

    @GetMapping("/admin/login")
    public String nqtLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        if ("noPermission".equals(error)) {
            model.addAttribute("nqtError", "Bạn không có quyền truy cập Admin! Chỉ nhân viên và admin mới được phép.");
        }
        // Set default value for 2FA section
        model.addAttribute("nqtShow2FA", false);
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String nqtLoginSubmit(@RequestParam(value = "nqtTaiKhoan", required = false) String nqtTaiKhoan,
            @RequestParam(value = "nqtMatKhau", required = false) String nqtMatKhau,
            @RequestParam(value = "nqt2faCode", required = false) String nqt2faCode,
            HttpSession session,
            HttpServletResponse response,
            Model model) {
        
        // Check if this is a 2FA verification (user already verified password)
        Integer pending2FAUserId = (Integer) session.getAttribute("nqtPending2FAUserId");
        if (pending2FAUserId != null && nqt2faCode != null && !nqt2faCode.isEmpty()) {
            // This is 2FA verification step
            Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(pending2FAUserId);
            if (userOptional.isPresent()) {
                NqtNguoiDung nqtNguoiDung = userOptional.get();
                
                // Check role again
                if (nqtNguoiDung.getNqtVaiTro() == null || 
                    (nqtNguoiDung.getNqtVaiTro() != 99 && nqtNguoiDung.getNqtVaiTro() != 1)) {
                    session.removeAttribute("nqtPending2FAUserId");
                    model.addAttribute("nqtError", "Bạn không có quyền truy cập Admin!");
                    model.addAttribute("nqtShow2FA", false);
                    return "admin/login";
                }
                
                String secret = nqtNguoiDung.getNqt2faSecret();
                if (secret != null && nqt2FAService.verifyCode(secret, nqt2faCode)) {
                    // 2FA verified, complete login
                    return completeAdminLogin(nqtNguoiDung, session, response);
                } else {
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
        
        // Tìm kiếm theo Tài khoản hoặc Email
        Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findByNqtTaiKhoanOrNqtEmail(cleanedUsername,
                cleanedUsername);

        if (nqtNguoiDungOptional.isPresent()) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungOptional.get();

            // Kiểm tra trạng thái hoạt động
            if (nqtNguoiDung.getNqtStatus() != null && !nqtNguoiDung.getNqtStatus()) {
                model.addAttribute("nqtError", "Tài khoản đã bị khóa hoặc chưa kích hoạt!");
                return "admin/login";
            }

            // Kiểm tra quyền trước
            if (nqtNguoiDung.getNqtVaiTro() == null || 
                (nqtNguoiDung.getNqtVaiTro() != 99 && nqtNguoiDung.getNqtVaiTro() != 1)) {
                model.addAttribute("nqtError", "Bạn không có quyền truy cập Admin!");
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
                model.addAttribute("nqtError", "Sai mật khẩu!");
            }
        } else {
            // Try to find by exact username first for better error message
            Optional<NqtNguoiDung> byUsername = nqtNguoiDungRepository.findByNqtTaiKhoan(cleanedUsername);
            Optional<NqtNguoiDung> byEmail = nqtNguoiDungRepository.findByNqtEmail(cleanedUsername);
            
            if (byUsername.isPresent() || byEmail.isPresent()) {
                model.addAttribute("nqtError", "Sai mật khẩu!");
            } else {
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
        return "redirect:/admin";
    }

    @GetMapping("/admin/logout")
    public String nqtLogout(HttpSession session, HttpServletResponse response) {
        // Clear SecurityContext (JWT authentication)
        SecurityContextHolder.clearContext();
        
        // Clear JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete cookie
        response.addCookie(jwtCookie);
        
        // Invalidate session (clears all session attributes)
        session.invalidate();
        
        return "redirect:/admin/login";
    }
}
