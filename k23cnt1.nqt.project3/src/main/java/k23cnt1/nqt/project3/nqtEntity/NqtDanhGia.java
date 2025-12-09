package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nqtDanhGia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDanhGia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;
    
    @ManyToOne
    @JoinColumn(name = "nqtDatPhongId", referencedColumnName = "nqtId")
    private NqtDatPhong nqtDatPhong;
    
    @Column(name = "nqtNoiDungDanhGia", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtNoiDungDanhGia;
    
    @Column(name = "nqtStatus")
    private Boolean nqtStatus; // 1-Hiện, 0-Ẩn
    
    @Column(name = "nqtNgayDanhGia")
    private LocalDateTime nqtNgayDanhGia;
}

