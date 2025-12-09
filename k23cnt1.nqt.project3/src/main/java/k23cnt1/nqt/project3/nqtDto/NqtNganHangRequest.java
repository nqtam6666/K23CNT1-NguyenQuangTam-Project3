package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtNganHangRequest {
    private String nqtTenNganHang;
    private String nqtMaNganHang; // Mã ngân hàng theo VietQR (VCB, TCB, VPB, etc.)
    private String nqtSoTaiKhoan;
    private String nqtTenChuTaiKhoan;
    private String nqtChiNhanh;
    private String nqtGhiChu;
    private Boolean nqtStatus;
    private Integer nqtThuTu;
}

