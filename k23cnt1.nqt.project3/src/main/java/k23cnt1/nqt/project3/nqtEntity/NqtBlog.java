package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nqtBlog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtBlog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;
    
    @Column(name = "nqtTieuDe", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtTieuDe;
    
    @Column(name = "nqtNoiDung", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtNoiDung;
    
    @Column(name = "nqtHinhAnh")
    private String nqtHinhAnh;
    
    @Column(name = "nqtNgayTao")
    private LocalDateTime nqtNgayTao;
    
    @Column(name = "nqtStatus")
    private Boolean nqtStatus; // 1-Hiện, 0-Ẩn
    
    @Column(name = "nqtMetaTitle", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMetaTitle;
    
    @Column(name = "nqtMetaKeyword", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMetaKeyword;
    
    @Column(name = "nqtMetaDescription", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMetaDescription;
}

