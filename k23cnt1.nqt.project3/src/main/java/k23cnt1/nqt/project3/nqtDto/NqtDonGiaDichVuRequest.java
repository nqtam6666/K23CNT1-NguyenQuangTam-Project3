package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDonGiaDichVuRequest {
    private Integer nqtSoLuong;
    private Float nqtThanhTien;
    private Integer nqtDatPhongId;
    private Integer nqtDichVuId;
}

