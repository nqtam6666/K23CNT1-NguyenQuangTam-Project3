package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nqtNguoiDung")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtNguoiDung {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;
    
    @Column(name = "nqtHoVaTen", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtHoVaTen;
    
    @Column(name = "nqtTaiKhoan", nullable = false, unique = true)
    private String nqtTaiKhoan;
    
    @Column(name = "nqtMatKhau", nullable = false)
    private String nqtMatKhau;
    
    @Column(name = "nqtSoDienThoai")
    private String nqtSoDienThoai;
    
    @Column(name = "nqtEmail")
    private String nqtEmail;
    
    @Column(name = "nqtDiaChi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtDiaChi;
    
    @Column(name = "nqtVaiTro")
    private Byte nqtVaiTro; // 0-Người dùng, 1-Nhân viên, 99-Admin
    
    @Column(name = "nqtStatus")
    private Boolean nqtStatus; // 1-Hoạt động, 0-Không hoạt động
}

