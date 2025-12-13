package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtGiamGiaRequest {
    private String nqtMaGiamGia;
    private String nqtMoTa;
    private Byte nqtLoaiGiam; // 0-Phần trăm, 1-Số tiền cố định
    private Float nqtGiaTriGiam;
    private Float nqtGiaTriToiThieu;
    private Float nqtGiaTriGiamToiDa;
    private LocalDate nqtNgayBatDau;
    private LocalDate nqtNgayKetThuc;
    private Integer nqtSoLuongToiDa;
    private Boolean nqtStatus;
    private Integer nqtNguoiDungId; // NULL = chung cho tất cả, có giá trị = riêng cho khách hàng
    private Boolean nqtChiChoVip; // true = chỉ áp dụng cho khách VIP
}

