package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nqtPhong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtPhong {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;
    
    @Column(name = "nqtSoPhong", nullable = false)
    private String nqtSoPhong;
    
    @Column(name = "nqtTenPhong", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtTenPhong;
    
    @ManyToOne
    @JoinColumn(name = "nqtLoaiPhongId", referencedColumnName = "nqtId")
    private NqtLoaiPhong nqtLoaiPhong;
    
    @Column(name = "nqtStatus")
    private Boolean nqtStatus; // 1-Trống, 0-Đã được book
}

