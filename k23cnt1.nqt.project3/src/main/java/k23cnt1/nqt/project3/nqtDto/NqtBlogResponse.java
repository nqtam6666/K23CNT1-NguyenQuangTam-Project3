package k23cnt1.nqt.project3.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtBlogResponse {
    private Integer nqtId;
    private String nqtTieuDe;
    private String nqtNoiDung;
    private String nqtHinhAnh;
    private LocalDateTime nqtNgayTao;
    private Boolean nqtStatus;
    private String nqtMetaTitle;
    private String nqtMetaKeyword;
    private String nqtMetaDescription;
}

