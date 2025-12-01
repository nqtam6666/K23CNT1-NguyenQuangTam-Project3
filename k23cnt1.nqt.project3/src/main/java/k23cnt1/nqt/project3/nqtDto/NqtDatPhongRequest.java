package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDatPhongRequest {
    private Integer nqtNguoiDungId;
    private Integer nqtPhongId;
    private LocalDate nqtNgayDen;
    private LocalDate nqtNgayDi;
    private Float nqtTongTien;
    private String nqtGhiChu;
    private Byte nqtStatus;
}

