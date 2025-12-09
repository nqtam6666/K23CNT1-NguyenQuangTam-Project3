package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private String nqtGhiChu;
    private Byte nqtStatus;
}

