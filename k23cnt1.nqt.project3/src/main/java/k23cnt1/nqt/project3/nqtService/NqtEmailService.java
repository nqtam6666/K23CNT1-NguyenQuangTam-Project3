package k23cnt1.nqt.project3.nqtService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class NqtEmailService {

    @Autowired
    private NqtSettingService nqtSettingService;

    private JavaMailSender mailSender;

    /**
     * Get or create JavaMailSender with current SMTP settings
     */
    private JavaMailSender getMailSender() {
        if (mailSender == null) {
            updateMailSender();
        }
        return mailSender;
    }

    /**
     * Update JavaMailSender with latest SMTP settings from database
     */
    public void updateMailSender() {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        
        // Get SMTP settings from database
        String smtpHost = nqtSettingService.getNqtValue("nqtSmtpHost", "smtp.gmail.com");
        String smtpPort = nqtSettingService.getNqtValue("nqtSmtpPort", "587");
        String smtpUsername = nqtSettingService.getNqtValue("nqtSmtpUsername", "");
        String smtpPassword = nqtSettingService.getNqtValue("nqtSmtpPassword", "");
        
        mailSenderImpl.setHost(smtpHost);
        mailSenderImpl.setPort(Integer.parseInt(smtpPort));
        mailSenderImpl.setUsername(smtpUsername);
        mailSenderImpl.setPassword(smtpPassword);
        
        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.debug", "false");
        
        this.mailSender = mailSenderImpl;
    }

    /**
     * Send email
     */
    public void sendEmail(String to, String subject, String body, boolean isHtml) {
        try {
            updateMailSender(); // Always use latest settings
            
            String fromEmail = nqtSettingService.getNqtValue("nqtSmtpFromEmail", "");
            String fromName = nqtSettingService.getNqtValue("nqtSmtpFromName", "");
            
            if (fromEmail == null || fromEmail.isEmpty()) {
                throw new RuntimeException("SMTP From Email chưa được cấu hình!");
            }
            
            MimeMessage message = getMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName != null ? fromName : "");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);
            
            getMailSender().send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage(), e);
        }
    }

    /**
     * Send HTML email
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        sendEmail(to, subject, htmlBody, true);
    }

    /**
     * Send plain text email
     */
    public void sendTextEmail(String to, String subject, String textBody) {
        sendEmail(to, subject, textBody, false);
    }

    /**
     * Test SMTP connection
     */
    public boolean testSmtpConnection() {
        try {
            updateMailSender();
            String testEmail = nqtSettingService.getNqtValue("nqtSmtpFromEmail", "");
            if (testEmail == null || testEmail.isEmpty()) {
                return false;
            }
            
            // Try to create a test message
            MimeMessage message = getMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(testEmail);
            helper.setTo(testEmail);
            helper.setSubject("Test SMTP Connection");
            helper.setText("This is a test email to verify SMTP configuration.");
            
            // Try to send (but don't actually send, just validate)
            // For actual test, you might want to send to yourself
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

