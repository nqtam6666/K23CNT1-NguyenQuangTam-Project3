package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtLoaiPhongResponse {
    private Integer nqtId;
    private String nqtTenLoaiPhong;
    private Float nqtGia;
    private Integer nqtSoNguoi;
    private String nqtHinhAnh;
    private Boolean nqtStatus;
    private String nqtMetaTitle;
    private String nqtMetaKeyword;
    private String nqtMetaDescription;
}

