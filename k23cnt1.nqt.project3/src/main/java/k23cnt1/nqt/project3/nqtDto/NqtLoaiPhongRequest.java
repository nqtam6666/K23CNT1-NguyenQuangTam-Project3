package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtLoaiPhongRequest {
    private String nqtTenLoaiPhong;
    private Float nqtGia;
    private Integer nqtSoNguoi;
    private String nqtHinhAnh;
    private Boolean nqtStatus;
    private String nqtMetaTitle;
    private String nqtMetaKeyword;
    private String nqtMetaDescription;
}

