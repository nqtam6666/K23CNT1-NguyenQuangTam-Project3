package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nqtDichVu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDichVu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;
    
    @Column(name = "nqtTen", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtTen;
    
    @Column(name = "nqtDonGia")
    private Float nqtDonGia;
    
    @Column(name = "nqtHinhAnh")
    private String nqtHinhAnh;
    
    @Column(name = "nqtStatus")
    private Boolean nqtStatus; // 1-Hoạt động, 0-Không hoạt động
    
    @Column(name = "nqtMetaTitle", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMetaTitle;
    
    @Column(name = "nqtMetaKeyword", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMetaKeyword;
    
    @Column(name = "nqtMetaDescription", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtMetaDescription;
}

