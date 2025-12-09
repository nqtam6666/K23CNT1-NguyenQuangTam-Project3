package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "nqtDatPhong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;

    @ManyToOne
    @JoinColumn(name = "nqtNguoiDungId", referencedColumnName = "nqtId")
    private NqtNguoiDung nqtNguoiDung;

    @ManyToOne
    @JoinColumn(name = "nqtPhongId", referencedColumnName = "nqtId")
    private NqtPhong nqtPhong;

    @Column(name = "nqtNgayDen")
    private LocalDate nqtNgayDen;

    @Column(name = "nqtNgayDi")
    private LocalDate nqtNgayDi;

    @Column(name = "nqtTongTien")
    private Float nqtTongTien;

    @Column(name = "nqtGiamGia")
    private Float nqtGiamGia; // Số tiền đã giảm (tính từ mã giảm giá hoặc chiết khấu VIP)

    @ManyToOne
    @JoinColumn(name = "nqtGiamGiaId", referencedColumnName = "nqtId")
    private NqtGiamGia nqtGiamGiaEntity; // Mã giảm giá đã sử dụng (nullable)

    @Column(name = "nqtGhiChu", columnDefinition = "VARCHAR(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtGhiChu;

    @Column(name = "nqtStatus")
    private Byte nqtStatus; // 0-Chưa thanh toán, 1-Đã thanh toán, 2-Hoàn tiền

    @OneToMany(mappedBy = "nqtDatPhong", cascade = CascadeType.ALL)
    private List<NqtDonGiaDichVu> nqtDonGiaDichVuList;

    @OneToMany(mappedBy = "nqtDatPhong", cascade = CascadeType.ALL)
    private List<NqtDanhGia> nqtDanhGiaList;
}
