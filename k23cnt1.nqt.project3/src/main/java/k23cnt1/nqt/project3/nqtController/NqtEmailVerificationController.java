package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.NqtEmailVerificationService;
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
    public String resendVerificationEmail(@RequestParam("email") String email, Model model) {
        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findByNqtEmail(email);
        
        if (userOptional.isPresent()) {
            NqtNguoiDung user = userOptional.get();
            if (user.getNqtEmailVerified() != null && user.getNqtEmailVerified()) {
                model.addAttribute("nqtError", "Email này đã được xác thực rồi!");
            } else {
                try {
                    nqtEmailVerificationService.sendVerificationEmail(user);
                    model.addAttribute("nqtSuccess", "Đã gửi lại email xác thực! Vui lòng kiểm tra hộp thư đến.");
                } catch (Exception e) {
                    model.addAttribute("nqtError", "Không thể gửi email: " + e.getMessage());
                }
            }
        } else {
            // Don't reveal if email exists (security)
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
    public String forgotPasswordSubmit(@RequestParam("nqtEmail") String email, Model model) {
        try {
            nqtEmailVerificationService.sendPasswordResetEmail(email);
            model.addAttribute("nqtSuccess", "Nếu email tồn tại, chúng tôi đã gửi link khôi phục mật khẩu đến email của bạn. Vui lòng kiểm tra hộp thư đến.");
        } catch (Exception e) {
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
                                     Model model) {
        // Validate passwords match
        if (!nqtMatKhauMoi.equals(nqtMatKhauXacNhan)) {
            model.addAttribute("nqtError", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("nqtToken", token);
            return "nqtCustomer/nqtAuth/nqtResetPassword";
        }

        Optional<NqtNguoiDung> userOptional = nqtEmailVerificationService.verifyPasswordResetToken(token);
        
        if (userOptional.isEmpty()) {
            model.addAttribute("nqtError", "Link khôi phục mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "nqtCustomer/nqtAuth/nqtForgotPassword";
        }

        NqtNguoiDung user = userOptional.get();
        user.setNqtMatKhau(passwordEncoder.encode(nqtMatKhauMoi));
        nqtNguoiDungRepository.save(user);

        // Mark token as used
        nqtEmailVerificationService.markPasswordResetTokenAsUsed(token);

        model.addAttribute("nqtSuccess", "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập ngay bây giờ.");
        return "nqtCustomer/nqtAuth/nqtLogin";
    }
}

