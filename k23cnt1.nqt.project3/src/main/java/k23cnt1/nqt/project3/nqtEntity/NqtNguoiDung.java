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

    @Column(name = "nqtCapBac", columnDefinition = "VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'KhachThuong'")
    private String nqtCapBac; // KhachThuong, KhachVip

    @Column(name = "nqt2faEnabled")
    private Boolean nqt2faEnabled; // 2FA enabled flag

    @Column(name = "nqt2faSecret", columnDefinition = "VARCHAR(255)")
    private String nqt2faSecret; // TOTP secret key

    @Column(name = "nqtEmailVerified")
    private Boolean nqtEmailVerified; // Email verification status

    @Column(name = "nqtPasswordResetToken", columnDefinition = "VARCHAR(255)")
    private String nqtPasswordResetToken; // Password reset token

    @Column(name = "nqtPasswordResetTokenExpiresAt")
    private java.time.LocalDateTime nqtPasswordResetTokenExpiresAt; // Token expiration time

    @OneToMany(mappedBy = "nqtNguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<NqtDatPhong> nqtDatPhongList;

    // Override toString() to avoid lazy loading exception
    @Override
    public String toString() {
        return "NqtNguoiDung{" +
                "nqtId=" + nqtId +
                ", nqtHoVaTen='" + nqtHoVaTen + '\'' +
                ", nqtTaiKhoan='" + nqtTaiKhoan + '\'' +
                ", nqtEmail='" + nqtEmail + '\'' +
                ", nqtVaiTro=" + nqtVaiTro +
                ", nqtStatus=" + nqtStatus +
                ", nqtCapBac='" + nqtCapBac + '\'' +
                '}';
    }
}
