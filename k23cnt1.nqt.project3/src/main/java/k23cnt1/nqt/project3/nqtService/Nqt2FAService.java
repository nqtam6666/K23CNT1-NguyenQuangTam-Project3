package k23cnt1.nqt.project3.nqtService;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class Nqt2FAService {

    @Value("${app.name:Hotel Management}")
    private String appName;

    /**
     * Generate a new secret for 2FA
     */
    public String generateSecret() {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        return secretGenerator.generate();
    }

    /**
     * Generate QR code data URI for 2FA setup
     */
    public String generateQrCodeDataUri(String secret, String email) {
        try {
            QrData data = new QrData.Builder()
                    .label(email)
                    .secret(secret)
                    .issuer(appName)
                    .algorithm(HashingAlgorithm.SHA1)
                    .digits(6)
                    .period(30)
                    .build();

            QrGenerator qrGenerator = new ZxingPngQrGenerator();
            byte[] qrCode = qrGenerator.generate(data);
            String base64QrCode = Base64.getEncoder().encodeToString(qrCode);
            return "data:image/png;base64," + base64QrCode;
        } catch (QrGenerationException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Verify OTP code
     */
    public boolean verifyCode(String secret, String code) {
        try {
            TimeProvider timeProvider = new SystemTimeProvider();
            CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
            CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
            
            // Allow time window of ±1 time step (30 seconds)
            return verifier.isValidCode(secret, code);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate current OTP code (for testing)
     */
    public String getCurrentCode(String secret) {
        try {
            TimeProvider timeProvider = new SystemTimeProvider();
            CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
            return codeGenerator.generate(secret, timeProvider.getTime());
        } catch (CodeGenerationException e) {
            throw new RuntimeException("Failed to generate code", e);
        }
    }
}

