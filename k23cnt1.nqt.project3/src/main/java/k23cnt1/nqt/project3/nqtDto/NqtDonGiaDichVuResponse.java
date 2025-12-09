package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDonGiaDichVuResponse {
    private Integer nqtId;
    private Integer nqtSoLuong;
    private Float nqtThanhTien;
    private Integer nqtDatPhongId;
    private String nqtSoPhong;
    private Integer nqtDichVuId;
    private String nqtTenDichVu;
}

