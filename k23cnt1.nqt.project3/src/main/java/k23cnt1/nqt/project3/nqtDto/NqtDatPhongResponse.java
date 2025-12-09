package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDatPhongResponse {
    private Integer nqtId;
    private Integer nqtNguoiDungId;
    private String nqtTenNguoiDung;
    private Integer nqtPhongId;
    private String nqtSoPhong;
    private LocalDate nqtNgayDen;
    private LocalDate nqtNgayDi;
    private Float nqtTongTien;
    private Float nqtGiamGia; // Số tiền đã giảm
    private Integer nqtGiamGiaId; // FK: Mã giảm giá đã sử dụng
    private String nqtMaGiamGia; // Mã giảm giá (từ entity)
    private String nqtGhiChu;
    private String nqtNoiDungChuyenKhoan; // Mã nội dung chuyển khoản
    private Byte nqtStatus;
    private LocalDateTime nqtNgayTao; // Thời gian tạo đơn đặt phòng
}

