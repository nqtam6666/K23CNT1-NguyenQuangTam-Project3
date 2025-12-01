package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtPhongRequest {
    private String nqtSoPhong;
    private String nqtTenPhong;
    private Integer nqtLoaiPhongId;
    private Boolean nqtStatus;
}

