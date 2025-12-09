package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtNganHangResponse {
    private Integer nqtId;
    private String nqtTenNganHang;
    private String nqtMaNganHang;
    private String nqtSoTaiKhoan;
    private String nqtTenChuTaiKhoan;
    private String nqtChiNhanh;
    private String nqtGhiChu;
    private Boolean nqtStatus;
    private Integer nqtThuTu;
    private LocalDateTime nqtNgayTao;
}

