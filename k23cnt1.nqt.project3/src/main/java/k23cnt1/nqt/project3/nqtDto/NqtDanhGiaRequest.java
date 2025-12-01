package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDanhGiaRequest {
    private Integer nqtDatPhongId;
    private String nqtNoiDungDanhGia;
    private Boolean nqtStatus;
}

