package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDatPhongRequest {
    private Integer nqtNguoiDungId;
    private Integer nqtPhongId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nqtNgayDen;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nqtNgayDi;

    private Float nqtTongTien;
    private String nqtGhiChu;
    private Byte nqtStatus;
}
