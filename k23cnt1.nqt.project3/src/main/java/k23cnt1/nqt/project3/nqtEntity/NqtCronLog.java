package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nqtCronLog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtCronLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;

    @Column(name = "nqtTaskName", nullable = false, length = 100)
    private String nqtTaskName; // Tên task (ví dụ: autoUpdateRoomStatus)

    @Column(name = "nqtStatus", nullable = false, length = 20)
    private String nqtStatus; // SUCCESS, ERROR, WARNING

    @Column(name = "nqtMessage", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMessage; // Thông báo chi tiết

    @Column(name = "nqtRecordsProcessed")
    private Integer nqtRecordsProcessed; // Số bản ghi đã xử lý

    @Column(name = "nqtExecutionTime")
    private Long nqtExecutionTime; // Thời gian thực thi (milliseconds)

    @Column(name = "nqtErrorDetails", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtErrorDetails; // Chi tiết lỗi nếu có

    @Column(name = "nqtCreatedAt", nullable = false)
    private LocalDateTime nqtCreatedAt;

    @PrePersist
    protected void onCreate() {
        nqtCreatedAt = LocalDateTime.now();
    }
}

