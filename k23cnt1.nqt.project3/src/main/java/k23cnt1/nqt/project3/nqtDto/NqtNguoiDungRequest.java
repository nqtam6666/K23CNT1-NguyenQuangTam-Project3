package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtNguoiDungRequest {
    private String nqtHoVaTen;
    private String nqtTaiKhoan;
    private String nqtMatKhau;
    private String nqtSoDienThoai;
    private String nqtEmail;
    private String nqtDiaChi;
    private Byte nqtVaiTro;
    private Boolean nqtStatus;
    private String nqtCapBac;
}

