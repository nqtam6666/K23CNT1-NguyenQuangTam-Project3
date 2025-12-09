package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nqtEmailToken")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtEmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;

    @Column(name = "nqtUserId", nullable = false)
    private Integer nqtUserId;

    @Column(name = "nqtToken", nullable = false, unique = true, length = 255)
    private String nqtToken;

    @Column(name = "nqtType", nullable = false, length = 50)
    private String nqtType; // EMAIL_VERIFICATION, PASSWORD_RESET

    @Column(name = "nqtExpiresAt", nullable = false)
    private LocalDateTime nqtExpiresAt;

    @Column(name = "nqtUsed")
    private Boolean nqtUsed; // true if token has been used

    @Column(name = "nqtCreatedAt")
    private LocalDateTime nqtCreatedAt;

    @PrePersist
    protected void onCreate() {
        nqtCreatedAt = LocalDateTime.now();
        if (nqtUsed == null) {
            nqtUsed = false;
        }
    }
}

