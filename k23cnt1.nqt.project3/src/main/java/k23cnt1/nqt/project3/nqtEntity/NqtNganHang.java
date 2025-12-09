package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nqtNganHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtNganHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;

    @Column(name = "nqtTenNganHang", nullable = false, length = 100)
    private String nqtTenNganHang; // Tên ngân hàng (ví dụ: Vietcombank, Techcombank)

    @Column(name = "nqtMaNganHang", nullable = false, length = 20)
    private String nqtMaNganHang; // Mã ngân hàng theo VietQR (ví dụ: VCB, TCB)

    @Column(name = "nqtSoTaiKhoan", nullable = false, length = 50)
    private String nqtSoTaiKhoan; // Số tài khoản

    @Column(name = "nqtTenChuTaiKhoan", nullable = false, length = 255)
    private String nqtTenChuTaiKhoan; // Tên chủ tài khoản

    @Column(name = "nqtChiNhanh", length = 255)
    private String nqtChiNhanh; // Chi nhánh (tùy chọn)

    @Column(name = "nqtGhiChu", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtGhiChu; // Ghi chú

    @Column(name = "nqtStatus", nullable = false)
    private Boolean nqtStatus = true; // 1-Hoạt động, 0-Không hoạt động

    @Column(name = "nqtThuTu", nullable = false)
    private Integer nqtThuTu = 0; // Thứ tự hiển thị

    @Column(name = "nqtNgayTao")
    private LocalDateTime nqtNgayTao;

    @PrePersist
    protected void onCreate() {
        nqtNgayTao = LocalDateTime.now();
        if (nqtStatus == null) {
            nqtStatus = true;
        }
        if (nqtThuTu == null) {
            nqtThuTu = 0;
        }
    }
}

