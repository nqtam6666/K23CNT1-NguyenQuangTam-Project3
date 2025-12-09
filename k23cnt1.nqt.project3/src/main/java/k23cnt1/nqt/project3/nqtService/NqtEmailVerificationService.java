package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtEntity.NqtEmailToken;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtEmailTokenRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class NqtEmailVerificationService {

    @Autowired
    private NqtEmailTokenRepository nqtEmailTokenRepository;

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private NqtEmailService nqtEmailService;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * Generate and send email verification token
     */
    @Transactional
    public void sendVerificationEmail(NqtNguoiDung user) {
        // Delete old unused tokens for this user
        nqtEmailTokenRepository.findByNqtUserIdAndNqtTypeAndNqtUsed(user.getNqtId(), "EMAIL_VERIFICATION", false)
            .ifPresent(oldToken -> nqtEmailTokenRepository.delete(oldToken));

        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24 hours validity

        NqtEmailToken emailToken = new NqtEmailToken();
        emailToken.setNqtUserId(user.getNqtId());
        emailToken.setNqtToken(token);
        emailToken.setNqtType("EMAIL_VERIFICATION");
        emailToken.setNqtExpiresAt(expiresAt);
        emailToken.setNqtUsed(false);
        nqtEmailTokenRepository.save(emailToken);

        // Send verification email
        String verificationUrl = "http://localhost:" + serverPort + contextPath + "/nqtXacThucEmail?token=" + token;
        String emailBody = buildVerificationEmailBody(user.getNqtHoVaTen(), verificationUrl);
        
        try {
            nqtEmailService.sendHtmlEmail(user.getNqtEmail(), "Xác thực email đăng ký tài khoản", emailBody);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email xác thực: " + e.getMessage());
        }
    }

    /**
     * Verify email token
     */
    @Transactional
    public boolean verifyEmail(String token) {
        Optional<NqtEmailToken> tokenOptional = nqtEmailTokenRepository.findByNqtTokenAndNqtType(token, "EMAIL_VERIFICATION");
        
        if (tokenOptional.isEmpty()) {
            return false;
        }

        NqtEmailToken emailToken = tokenOptional.get();
        
        // Check if token is expired
        if (emailToken.getNqtExpiresAt().isBefore(LocalDateTime.now())) {
            nqtEmailTokenRepository.delete(emailToken);
            return false;
        }

        // Check if token is already used
        if (emailToken.getNqtUsed()) {
            return false;
        }

        // Verify email
        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findById(emailToken.getNqtUserId());
        if (userOptional.isPresent()) {
            NqtNguoiDung user = userOptional.get();
            user.setNqtEmailVerified(true);
            user.setNqtStatus(true); // Activate account
            nqtNguoiDungRepository.save(user);

            // Mark token as used
            emailToken.setNqtUsed(true);
            nqtEmailTokenRepository.save(emailToken);
            
            return true;
        }

        return false;
    }

    /**
     * Generate and send password reset token
     */
    @Transactional
    public void sendPasswordResetEmail(String email) {
        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findByNqtEmail(email);
        
        if (userOptional.isEmpty()) {
            // Don't reveal if email exists or not (security best practice)
            return;
        }

        NqtNguoiDung user = userOptional.get();

        // Delete old unused tokens for this user (from nqtEmailToken table)
        nqtEmailTokenRepository.findByNqtUserIdAndNqtTypeAndNqtUsed(user.getNqtId(), "PASSWORD_RESET", false)
            .ifPresent(oldToken -> nqtEmailTokenRepository.delete(oldToken));

        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // 1 hour validity

        // Save token in nqtEmailToken table (for history tracking)
        NqtEmailToken emailToken = new NqtEmailToken();
        emailToken.setNqtUserId(user.getNqtId());
        emailToken.setNqtToken(token);
        emailToken.setNqtType("PASSWORD_RESET");
        emailToken.setNqtExpiresAt(expiresAt);
        emailToken.setNqtUsed(false);
        nqtEmailTokenRepository.save(emailToken);

        // Also save token directly in user table for easier query
        user.setNqtPasswordResetToken(token);
        user.setNqtPasswordResetTokenExpiresAt(expiresAt);
        nqtNguoiDungRepository.save(user);

        // Send reset email
        String resetUrl = "http://localhost:" + serverPort + contextPath + "/nqtQuenMatKhau/reset?token=" + token;
        String emailBody = buildPasswordResetEmailBody(user.getNqtHoVaTen(), resetUrl);
        
        try {
            nqtEmailService.sendHtmlEmail(user.getNqtEmail(), "Khôi phục mật khẩu", emailBody);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email khôi phục mật khẩu: " + e.getMessage());
        }
    }

    /**
     * Verify password reset token
     * First check in user table (faster), then fallback to token table
     */
    public Optional<NqtNguoiDung> verifyPasswordResetToken(String token) {
        // First, try to find user by token in user table (faster query)
        Optional<NqtNguoiDung> userByToken = nqtNguoiDungRepository.findByNqtPasswordResetToken(token);
        
        if (userByToken.isPresent()) {
            NqtNguoiDung user = userByToken.get();
            
            // Check if token is expired
            if (user.getNqtPasswordResetTokenExpiresAt() != null && 
                user.getNqtPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
                // Clear expired token
                user.setNqtPasswordResetToken(null);
                user.setNqtPasswordResetTokenExpiresAt(null);
                nqtNguoiDungRepository.save(user);
                return Optional.empty();
            }
            
            return Optional.of(user);
        }
        
        // Fallback: check in nqtEmailToken table (for backward compatibility)
        Optional<NqtEmailToken> tokenOptional = nqtEmailTokenRepository.findByNqtTokenAndNqtType(token, "PASSWORD_RESET");
        
        if (tokenOptional.isEmpty()) {
            return Optional.empty();
        }

        NqtEmailToken emailToken = tokenOptional.get();
        
        // Check if token is expired
        if (emailToken.getNqtExpiresAt().isBefore(LocalDateTime.now())) {
            nqtEmailTokenRepository.delete(emailToken);
            return Optional.empty();
        }

        // Check if token is already used
        if (emailToken.getNqtUsed()) {
            return Optional.empty();
        }

        return nqtNguoiDungRepository.findById(emailToken.getNqtUserId());
    }

    /**
     * Mark password reset token as used
     */
    @Transactional
    public void markPasswordResetTokenAsUsed(String token) {
        // Clear token from user table
        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findByNqtPasswordResetToken(token);
        
        if (userOptional.isPresent()) {
            NqtNguoiDung user = userOptional.get();
            user.setNqtPasswordResetToken(null);
            user.setNqtPasswordResetTokenExpiresAt(null);
            nqtNguoiDungRepository.save(user);
        }
        
        // Also mark as used in token table (for history)
        nqtEmailTokenRepository.findByNqtTokenAndNqtType(token, "PASSWORD_RESET")
            .ifPresent(emailToken -> {
                emailToken.setNqtUsed(true);
                nqtEmailTokenRepository.save(emailToken);
            });
    }

    private String buildVerificationEmailBody(String name, String verificationUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #d97706;'>Xác thực email đăng ký tài khoản</h2>" +
                "<p>Xin chào <strong>" + (name != null ? name : "Bạn") + "</strong>,</p>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản. Vui lòng click vào link bên dưới để xác thực email của bạn:</p>" +
                "<p style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + verificationUrl + "' style='background-color: #d97706; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Xác thực email</a>" +
                "</p>" +
                "<p>Hoặc copy và paste link sau vào trình duyệt:</p>" +
                "<p style='word-break: break-all; color: #666;'>" + verificationUrl + "</p>" +
                "<p style='color: #999; font-size: 12px; margin-top: 30px;'>Link này sẽ hết hạn sau 24 giờ.</p>" +
                "<p style='color: #999; font-size: 12px;'>Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildPasswordResetEmailBody(String name, String resetUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #d97706;'>Khôi phục mật khẩu</h2>" +
                "<p>Xin chào <strong>" + (name != null ? name : "Bạn") + "</strong>,</p>" +
                "<p>Chúng tôi nhận được yêu cầu khôi phục mật khẩu cho tài khoản của bạn. Click vào link bên dưới để đặt lại mật khẩu:</p>" +
                "<p style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetUrl + "' style='background-color: #d97706; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Đặt lại mật khẩu</a>" +
                "</p>" +
                "<p>Hoặc copy và paste link sau vào trình duyệt:</p>" +
                "<p style='word-break: break-all; color: #666;'>" + resetUrl + "</p>" +
                "<p style='color: #999; font-size: 12px; margin-top: 30px;'>Link này sẽ hết hạn sau 1 giờ.</p>" +
                "<p style='color: #999; font-size: 12px;'>Nếu bạn không yêu cầu khôi phục mật khẩu, vui lòng bỏ qua email này. Mật khẩu của bạn sẽ không thay đổi.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

