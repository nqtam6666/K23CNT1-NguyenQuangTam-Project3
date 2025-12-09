package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nqtsetting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;

    @Column(name = "nqtName", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtName;

    @Column(name = "nqtValue", columnDefinition = "VARCHAR(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nqtValue;
}
