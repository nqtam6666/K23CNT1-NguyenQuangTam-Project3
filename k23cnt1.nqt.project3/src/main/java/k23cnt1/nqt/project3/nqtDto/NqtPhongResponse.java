package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtPhongResponse {
    private Integer nqtId;
    private String nqtSoPhong;
    private String nqtTenPhong;
    private Integer nqtLoaiPhongId;
    private String nqtTenLoaiPhong;
    private Boolean nqtStatus;
}

