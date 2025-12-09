package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDanhGiaResponse {
    private Integer nqtId;
    private Integer nqtDatPhongId;
    private String nqtSoPhong;
    private String nqtTenNguoiDung;
    private String nqtNoiDungDanhGia;
    private Boolean nqtStatus;
    private LocalDateTime nqtNgayDanhGia;
}

