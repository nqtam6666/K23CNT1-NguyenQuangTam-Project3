package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.Nqt2FAService;
import k23cnt1.nqt.project3.nqtService.NqtRateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class Nqt2FAController {

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private Nqt2FAService nqt2FAService;

    @Autowired
    private NqtRateLimitService rateLimitService;

    // Setup 2FA - Generate QR Code
    @GetMapping("/nqtTaiKhoan/setup-2fa")
    public String setup2FA(HttpSession session, Model model) {
        NqtNguoiDung user = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");
        if (user == null) {
            return "redirect:/nqtDangNhap";
        }

        // Refresh user from database
        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(user.getNqtId());
        if (userOptional.isEmpty()) {
            return "redirect:/nqtDangNhap";
        }

        user = userOptional.get();
        
        // If 2FA already enabled, show current status
        if (user.getNqt2faEnabled() != null && user.getNqt2faEnabled()) {
            model.addAttribute("nqt2faEnabled", true);
            return "nqtCustomer/nqtTaiKhoan/nqt2FASetup";
        }

        // Generate new secret
        String secret = nqt2FAService.generateSecret();
        String qrCodeDataUri = nqt2FAService.generateQrCodeDataUri(secret, user.getNqtEmail());
        
        // Store secret temporarily in session
        session.setAttribute("nqt2faPendingSecret", secret);
        
        model.addAttribute("nqtSecret", secret);
        model.addAttribute("nqtQrCode", qrCodeDataUri);
        model.addAttribute("nqtEmail", user.getNqtEmail());
        
        return "nqtCustomer/nqtTaiKhoan/nqt2FASetup";
    }

    // Verify and Enable 2FA
    @PostMapping("/nqtTaiKhoan/enable-2fa")
    public String enable2FA(@RequestParam("nqt2faCode") String code,
                           HttpServletRequest request,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        NqtNguoiDung user = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");
        if (user == null) {
            return "redirect:/nqtDangNhap";
        }

        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        String identifier = user.getNqtEmail() != null ? user.getNqtEmail() : user.getNqtTaiKhoan();

        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (IP)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", ipRateLimit.getMessage());
            return "redirect:/nqtTaiKhoan/setup-2fa";
        }
        
        // Check rate limiting for identifier
        NqtRateLimitService.RateLimitResult identifierRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            identifier, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (identifierRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (identifier)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", identifierRateLimit.getMessage());
            return "redirect:/nqtTaiKhoan/setup-2fa";
        }

        String pendingSecret = (String) session.getAttribute("nqt2faPendingSecret");
        if (pendingSecret == null) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Session expired", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", "Phiên làm việc đã hết hạn. Vui lòng thử lại!");
            return "redirect:/nqtTaiKhoan/setup-2fa";
        }

        // Verify code
        if (nqt2FAService.verifyCode(pendingSecret, code)) {
            // Enable 2FA
            Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(user.getNqtId());
            if (userOptional.isPresent()) {
                NqtNguoiDung dbUser = userOptional.get();
                dbUser.setNqt2faEnabled(true);
                dbUser.setNqt2faSecret(pendingSecret);
                nqtNguoiDungRepository.save(dbUser);
                
                // Update session
                session.setAttribute("nqtCustomerUser", dbUser);
                session.removeAttribute("nqt2faPendingSecret");
                
                rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent,
                                              NqtRateLimitService.ACTION_2FA_VERIFICATION);
                redirectAttributes.addFlashAttribute("nqtSuccess", "Đã bật xác thực 2FA thành công!");
            }
        } else {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", "Mã xác thực không đúng! Vui lòng thử lại.");
            return "redirect:/nqtTaiKhoan/setup-2fa";
        }

        return "redirect:/nqtTaiKhoan";
    }

    // Disable 2FA
    @PostMapping("/nqtTaiKhoan/disable-2fa")
    public String disable2FA(@RequestParam("nqt2faCode") String code,
                            HttpServletRequest request,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        NqtNguoiDung user = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");
        if (user == null) {
            return "redirect:/nqtDangNhap";
        }

        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        String identifier = user.getNqtEmail() != null ? user.getNqtEmail() : user.getNqtTaiKhoan();

        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (IP)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", ipRateLimit.getMessage());
            return "redirect:/nqtTaiKhoan";
        }
        
        // Check rate limiting for identifier
        NqtRateLimitService.RateLimitResult identifierRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            identifier, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (identifierRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (identifier)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", identifierRateLimit.getMessage());
            return "redirect:/nqtTaiKhoan";
        }

        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(user.getNqtId());
        if (userOptional.isEmpty()) {
            return "redirect:/nqtDangNhap";
        }

        NqtNguoiDung dbUser = userOptional.get();
        
        // Verify code before disabling
        if (dbUser.getNqt2faSecret() != null && nqt2FAService.verifyCode(dbUser.getNqt2faSecret(), code)) {
            dbUser.setNqt2faEnabled(false);
            dbUser.setNqt2faSecret(null);
            nqtNguoiDungRepository.save(dbUser);
            
            // Update session
            session.setAttribute("nqtCustomerUser", dbUser);
            
            rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã tắt xác thực 2FA thành công!");
        } else {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", "Mã xác thực không đúng!");
        }

        return "redirect:/nqtTaiKhoan";
    }

    // Admin 2FA Setup
    @GetMapping("/admin/setup-2fa")
    public String adminSetup2FA(HttpSession session, Model model) {
        NqtNguoiDung user = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
        if (user == null) {
            return "redirect:/admin/login";
        }

        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(user.getNqtId());
        if (userOptional.isEmpty()) {
            return "redirect:/admin/login";
        }

        user = userOptional.get();
        
        if (user.getNqt2faEnabled() != null && user.getNqt2faEnabled()) {
            model.addAttribute("nqt2faEnabled", true);
            return "admin/2fa/setup";
        }

        String secret = nqt2FAService.generateSecret();
        String qrCodeDataUri = nqt2FAService.generateQrCodeDataUri(secret, user.getNqtEmail());
        
        session.setAttribute("nqt2faPendingSecret", secret);
        
        model.addAttribute("nqtSecret", secret);
        model.addAttribute("nqtQrCode", qrCodeDataUri);
        model.addAttribute("nqtEmail", user.getNqtEmail());
        
        return "admin/2fa/setup";
    }

    @PostMapping("/admin/enable-2fa")
    public String adminEnable2FA(@RequestParam("nqt2faCode") String code,
                                HttpServletRequest request,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        NqtNguoiDung user = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
        if (user == null) {
            return "redirect:/admin/login";
        }

        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        String identifier = user.getNqtEmail() != null ? user.getNqtEmail() : user.getNqtTaiKhoan();

        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (IP)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", ipRateLimit.getMessage());
            return "redirect:/admin/setup-2fa";
        }
        
        // Check rate limiting for identifier
        NqtRateLimitService.RateLimitResult identifierRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            identifier, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (identifierRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (identifier)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", identifierRateLimit.getMessage());
            return "redirect:/admin/setup-2fa";
        }

        String pendingSecret = (String) session.getAttribute("nqt2faPendingSecret");
        if (pendingSecret == null) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Session expired", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", "Phiên làm việc đã hết hạn. Vui lòng thử lại!");
            return "redirect:/admin/setup-2fa";
        }

        if (nqt2FAService.verifyCode(pendingSecret, code)) {
            Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(user.getNqtId());
            if (userOptional.isPresent()) {
                NqtNguoiDung dbUser = userOptional.get();
                dbUser.setNqt2faEnabled(true);
                dbUser.setNqt2faSecret(pendingSecret);
                nqtNguoiDungRepository.save(dbUser);
                
                session.setAttribute("nqtAdminUser", dbUser);
                session.removeAttribute("nqt2faPendingSecret");
                
                rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent,
                                              NqtRateLimitService.ACTION_2FA_VERIFICATION);
                redirectAttributes.addFlashAttribute("nqtSuccess", "Đã bật xác thực 2FA thành công!");
            }
        } else {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", "Mã xác thực không đúng! Vui lòng thử lại.");
            return "redirect:/admin/setup-2fa";
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/disable-2fa")
    public String adminDisable2FA(@RequestParam("nqt2faCode") String code,
                                  HttpServletRequest request,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        NqtNguoiDung user = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
        if (user == null) {
            return "redirect:/admin/login";
        }

        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        String identifier = user.getNqtEmail() != null ? user.getNqtEmail() : user.getNqtTaiKhoan();

        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (IP)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", ipRateLimit.getMessage());
            return "redirect:/admin";
        }
        
        // Check rate limiting for identifier
        NqtRateLimitService.RateLimitResult identifierRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            identifier, NqtRateLimitService.ACTION_2FA_VERIFICATION,
            "Quá nhiều lần thử xác thực 2FA. Vui lòng đợi một chút trước khi thử lại.");
        if (identifierRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (identifier)", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", identifierRateLimit.getMessage());
            return "redirect:/admin";
        }

        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(user.getNqtId());
        if (userOptional.isEmpty()) {
            return "redirect:/admin/login";
        }

        NqtNguoiDung dbUser = userOptional.get();
        
        if (dbUser.getNqt2faSecret() != null && nqt2FAService.verifyCode(dbUser.getNqt2faSecret(), code)) {
            dbUser.setNqt2faEnabled(false);
            dbUser.setNqt2faSecret(null);
            nqtNguoiDungRepository.save(dbUser);
            
            session.setAttribute("nqtAdminUser", dbUser);
            
            rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã tắt xác thực 2FA thành công!");
        } else {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Invalid 2FA code", userAgent,
                                          NqtRateLimitService.ACTION_2FA_VERIFICATION);
            redirectAttributes.addFlashAttribute("nqtError", "Mã xác thực không đúng!");
        }

        return "redirect:/admin";
    }
}

