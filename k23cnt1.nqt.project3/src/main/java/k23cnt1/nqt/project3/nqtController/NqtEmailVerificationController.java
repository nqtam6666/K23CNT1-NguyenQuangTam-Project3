package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.NqtEmailVerificationService;
import k23cnt1.nqt.project3.nqtService.NqtRateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class NqtEmailVerificationController {

    @Autowired
    private NqtEmailVerificationService nqtEmailVerificationService;

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NqtRateLimitService rateLimitService;

    // Email Verification
    @GetMapping("/nqtXacThucEmail")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        boolean verified = nqtEmailVerificationService.verifyEmail(token);
        
        if (verified) {
            model.addAttribute("nqtSuccess", "Email đã được xác thực thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } else {
            model.addAttribute("nqtError", "Link xác thực không hợp lệ hoặc đã hết hạn. Vui lòng đăng ký lại hoặc yêu cầu gửi lại email xác thực.");
        }
        
        return "nqtCustomer/nqtAuth/nqtEmailVerification";
    }

    // Resend verification email
    @GetMapping("/nqtGuiLaiEmailXacThuc")
    public String resendVerificationEmail(@RequestParam("email") String email, 
                                         HttpServletRequest request,
                                         Model model) {
        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        
        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_EMAIL_VERIFICATION,
            "Quá nhiều lần yêu cầu gửi lại email. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(email, ipAddress, false, "Rate limit exceeded (IP)", userAgent, 
                                          NqtRateLimitService.ACTION_EMAIL_VERIFICATION);
            model.addAttribute("nqtError", ipRateLimit.getMessage());
            return "nqtCustomer/nqtAuth/nqtEmailVerification";
        }
        
        // Check rate limiting for email
        NqtRateLimitService.RateLimitResult emailRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            email, NqtRateLimitService.ACTION_EMAIL_VERIFICATION,
            "Quá nhiều lần yêu cầu gửi lại email cho địa chỉ này. Vui lòng đợi một chút trước khi thử lại.");
        if (emailRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(email, ipAddress, false, "Rate limit exceeded (email)", userAgent,
                                          NqtRateLimitService.ACTION_EMAIL_VERIFICATION);
            model.addAttribute("nqtError", emailRateLimit.getMessage());
            return "nqtCustomer/nqtAuth/nqtEmailVerification";
        }
        
        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findByNqtEmail(email);
        
        if (userOptional.isPresent()) {
            NqtNguoiDung user = userOptional.get();
            if (user.getNqtEmailVerified() != null && user.getNqtEmailVerified()) {
                rateLimitService.recordAttempt(email, ipAddress, false, "Email already verified", userAgent,
                                              NqtRateLimitService.ACTION_EMAIL_VERIFICATION);
                model.addAttribute("nqtError", "Email này đã được xác thực rồi!");
            } else {
                try {
                    nqtEmailVerificationService.sendVerificationEmail(user);
                    rateLimitService.recordAttempt(email, ipAddress, true, null, userAgent,
                                                  NqtRateLimitService.ACTION_EMAIL_VERIFICATION);
                    model.addAttribute("nqtSuccess", "Đã gửi lại email xác thực! Vui lòng kiểm tra hộp thư đến.");
                } catch (Exception e) {
                    rateLimitService.recordAttempt(email, ipAddress, false, "Email send failed: " + e.getMessage(), userAgent,
                                                  NqtRateLimitService.ACTION_EMAIL_VERIFICATION);
                    model.addAttribute("nqtError", "Không thể gửi email: " + e.getMessage());
                }
            }
        } else {
            // Don't reveal if email exists (security) - but still record attempt
            rateLimitService.recordAttempt(email, ipAddress, true, null, userAgent,
                                          NqtRateLimitService.ACTION_EMAIL_VERIFICATION);
            model.addAttribute("nqtSuccess", "Nếu email tồn tại, chúng tôi đã gửi lại email xác thực.");
        }
        
        return "nqtCustomer/nqtAuth/nqtEmailVerification";
    }

    // Forgot Password - Request
    @GetMapping("/nqtQuenMatKhau")
    public String forgotPassword() {
        return "nqtCustomer/nqtAuth/nqtForgotPassword";
    }

    @PostMapping("/nqtQuenMatKhau")
    public String forgotPasswordSubmit(@RequestParam("nqtEmail") String email, 
                                      HttpServletRequest request,
                                      Model model) {
        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        
        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_FORGOT_PASSWORD,
            "Quá nhiều lần yêu cầu quên mật khẩu. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(email, ipAddress, false, "Rate limit exceeded (IP)", userAgent,
                                          NqtRateLimitService.ACTION_FORGOT_PASSWORD);
            model.addAttribute("nqtError", ipRateLimit.getMessage());
            return "nqtCustomer/nqtAuth/nqtForgotPassword";
        }
        
        // Check rate limiting for email
        NqtRateLimitService.RateLimitResult emailRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            email, NqtRateLimitService.ACTION_FORGOT_PASSWORD,
            "Quá nhiều lần yêu cầu quên mật khẩu cho email này. Vui lòng đợi một chút trước khi thử lại.");
        if (emailRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(email, ipAddress, false, "Rate limit exceeded (email)", userAgent,
                                          NqtRateLimitService.ACTION_FORGOT_PASSWORD);
            model.addAttribute("nqtError", emailRateLimit.getMessage());
            return "nqtCustomer/nqtAuth/nqtForgotPassword";
        }
        
        try {
            nqtEmailVerificationService.sendPasswordResetEmail(email);
            rateLimitService.recordAttempt(email, ipAddress, true, null, userAgent,
                                          NqtRateLimitService.ACTION_FORGOT_PASSWORD);
            model.addAttribute("nqtSuccess", "Nếu email tồn tại, chúng tôi đã gửi link khôi phục mật khẩu đến email của bạn. Vui lòng kiểm tra hộp thư đến.");
        } catch (Exception e) {
            rateLimitService.recordAttempt(email, ipAddress, false, "Email send failed: " + e.getMessage(), userAgent,
                                          NqtRateLimitService.ACTION_FORGOT_PASSWORD);
            model.addAttribute("nqtError", "Không thể gửi email: " + e.getMessage());
        }
        
        return "nqtCustomer/nqtAuth/nqtForgotPassword";
    }

    // Reset Password - Form
    @GetMapping("/nqtQuenMatKhau/reset")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<NqtNguoiDung> userOptional = nqtEmailVerificationService.verifyPasswordResetToken(token);
        
        if (userOptional.isEmpty()) {
            model.addAttribute("nqtError", "Link khôi phục mật khẩu không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu lại.");
            return "nqtCustomer/nqtAuth/nqtForgotPassword";
        }
        
        model.addAttribute("nqtToken", token);
        return "nqtCustomer/nqtAuth/nqtResetPassword";
    }

    @PostMapping("/nqtQuenMatKhau/reset")
    public String resetPasswordSubmit(@RequestParam("token") String token,
                                     @RequestParam("nqtMatKhauMoi") String nqtMatKhauMoi,
                                     @RequestParam("nqtMatKhauXacNhan") String nqtMatKhauXacNhan,
                                     HttpServletRequest request,
                                     Model model) {
        String ipAddress = rateLimitService.getClientIpAddress(request);
        String userAgent = rateLimitService.getUserAgent(request);
        
        // Validate passwords match
        if (!nqtMatKhauMoi.equals(nqtMatKhauXacNhan)) {
            model.addAttribute("nqtError", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("nqtToken", token);
            return "nqtCustomer/nqtAuth/nqtResetPassword";
        }

        Optional<NqtNguoiDung> userOptional = nqtEmailVerificationService.verifyPasswordResetToken(token);
        
        if (userOptional.isEmpty()) {
            rateLimitService.recordAttempt("unknown", ipAddress, false, "Invalid or expired token", userAgent,
                                          NqtRateLimitService.ACTION_RESET_PASSWORD);
            model.addAttribute("nqtError", "Link khôi phục mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "nqtCustomer/nqtAuth/nqtForgotPassword";
        }

        NqtNguoiDung user = userOptional.get();
        String identifier = user.getNqtEmail() != null ? user.getNqtEmail() : user.getNqtTaiKhoan();
        
        // Check rate limiting for IP
        NqtRateLimitService.RateLimitResult ipRateLimit = rateLimitService.checkRateLimitByIpAddressAndAction(
            ipAddress, NqtRateLimitService.ACTION_RESET_PASSWORD,
            "Quá nhiều lần đặt lại mật khẩu. Vui lòng đợi một chút trước khi thử lại.");
        if (ipRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (IP)", userAgent,
                                          NqtRateLimitService.ACTION_RESET_PASSWORD);
            model.addAttribute("nqtError", ipRateLimit.getMessage());
            model.addAttribute("nqtToken", token);
            return "nqtCustomer/nqtAuth/nqtResetPassword";
        }
        
        // Check rate limiting for identifier
        NqtRateLimitService.RateLimitResult emailRateLimit = rateLimitService.checkRateLimitByIdentifierAndAction(
            identifier, NqtRateLimitService.ACTION_RESET_PASSWORD,
            "Quá nhiều lần đặt lại mật khẩu cho tài khoản này. Vui lòng đợi một chút trước khi thử lại.");
        if (emailRateLimit.isBlocked()) {
            rateLimitService.recordAttempt(identifier, ipAddress, false, "Rate limit exceeded (identifier)", userAgent,
                                          NqtRateLimitService.ACTION_RESET_PASSWORD);
            model.addAttribute("nqtError", emailRateLimit.getMessage());
            model.addAttribute("nqtToken", token);
            return "nqtCustomer/nqtAuth/nqtResetPassword";
        }

        user.setNqtMatKhau(passwordEncoder.encode(nqtMatKhauMoi));
        nqtNguoiDungRepository.save(user);

        // Mark token as used
        nqtEmailVerificationService.markPasswordResetTokenAsUsed(token);

        rateLimitService.recordAttempt(identifier, ipAddress, true, null, userAgent,
                                      NqtRateLimitService.ACTION_RESET_PASSWORD);
        model.addAttribute("nqtSuccess", "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập ngay bây giờ.");
        return "nqtCustomer/nqtAuth/nqtLogin";
    }
}

