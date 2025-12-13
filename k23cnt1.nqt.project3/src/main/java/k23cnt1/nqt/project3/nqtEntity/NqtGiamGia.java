package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nqtGiamGia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;

    @Column(name = "nqtMaGiamGia", nullable = false, unique = true, length = 50)
    private String nqtMaGiamGia; // Mã giảm giá (ví dụ: VIP2024, SUMMER10)

    @Column(name = "nqtMoTa", columnDefinition = "VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMoTa; // Mô tả mã giảm giá

    @Column(name = "nqtLoaiGiam", nullable = false)
    private Byte nqtLoaiGiam; // 0-Phần trăm, 1-Số tiền cố định

    @Column(name = "nqtGiaTriGiam", nullable = false)
    private Float nqtGiaTriGiam; // Giá trị giảm (phần trăm hoặc số tiền)

    @Column(name = "nqtGiaTriToiThieu")
    private Float nqtGiaTriToiThieu; // Giá trị đơn hàng tối thiểu để áp dụng (nullable)

    @Column(name = "nqtGiaTriGiamToiDa")
    private Float nqtGiaTriGiamToiDa; // Giá trị giảm tối đa (nullable, chỉ áp dụng khi loại là phần trăm)

    @Column(name = "nqtNgayBatDau")
    private LocalDate nqtNgayBatDau; // Ngày bắt đầu áp dụng

    @Column(name = "nqtNgayKetThuc")
    private LocalDate nqtNgayKetThuc; // Ngày kết thúc

    @Column(name = "nqtSoLuongToiDa")
    private Integer nqtSoLuongToiDa; // Số lần sử dụng tối đa (nullable = không giới hạn)

    @Column(name = "nqtSoLuongDaDung", nullable = false)
    private Integer nqtSoLuongDaDung = 0; // Số lần đã sử dụng

    @Column(name = "nqtStatus", nullable = false)
    private Boolean nqtStatus = true; // 1-Hoạt động, 0-Không hoạt động

    @Column(name = "nqtNgayTao")
    private LocalDateTime nqtNgayTao; // Ngày tạo mã

    // Foreign key: Nếu NULL thì mã chung cho tất cả, nếu có giá trị thì mã riêng cho khách hàng đó
    @ManyToOne
    @JoinColumn(name = "nqtNguoiDungId", referencedColumnName = "nqtId")
    private NqtNguoiDung nqtNguoiDung;
    
    @Column(name = "nqtChiChoVip")
    private Boolean nqtChiChoVip = false; // Nếu true thì chỉ áp dụng cho khách VIP

    @PrePersist
    protected void onCreate() {
        nqtNgayTao = LocalDateTime.now();
        if (nqtSoLuongDaDung == null) {
            nqtSoLuongDaDung = 0;
        }
        if (nqtStatus == null) {
            nqtStatus = true;
        }
    }
}

