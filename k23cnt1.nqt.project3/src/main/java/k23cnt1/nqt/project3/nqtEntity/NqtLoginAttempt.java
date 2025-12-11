package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nqtloginattempt", indexes = {
    @Index(name = "idx_identifier", columnList = "nqtIdentifier"),
    @Index(name = "idx_ip_address", columnList = "nqtIpAddress"),
    @Index(name = "idx_attempt_time", columnList = "nqtAttemptTime")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtLoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;

    @Column(name = "nqtIdentifier", length = 255, nullable = false)
    private String nqtIdentifier; // Username, email, or user ID

    @Column(name = "nqtIpAddress", length = 45)
    private String nqtIpAddress; // IPv4 or IPv6

    @Column(name = "nqtAttemptTime", nullable = false)
    private LocalDateTime nqtAttemptTime;

    @Column(name = "nqtSuccess", nullable = false)
    private Boolean nqtSuccess; // true if login successful, false if failed

    @Column(name = "nqtFailureReason", length = 500)
    private String nqtFailureReason; // Reason for failure (wrong password, account locked, etc.)

    @Column(name = "nqtUserAgent", length = 500)
    private String nqtUserAgent; // Browser/device info

    @Column(name = "nqtActionType", length = 50)
    private String nqtActionType; // Action type: login, register, forgot_password, reset_password, email_verification, 2fa_verification

    @PrePersist
    protected void onCreate() {
        if (nqtAttemptTime == null) {
            nqtAttemptTime = LocalDateTime.now();
        }
    }
}

