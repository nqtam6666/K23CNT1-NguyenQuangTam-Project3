package k23cnt1.nqt.project3.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nqtDonGiaDichVu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NqtDonGiaDichVu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nqtId")
    private Integer nqtId;
    
    @Column(name = "nqtSoLuong")
    private Integer nqtSoLuong;
    
    @Column(name = "nqtThanhTien")
    private Float nqtThanhTien;
    
    @ManyToOne
    @JoinColumn(name = "nqtDatPhongId", referencedColumnName = "nqtId")
    private NqtDatPhong nqtDatPhong;
    
    @ManyToOne
    @JoinColumn(name = "nqtDichVuId", referencedColumnName = "nqtId")
    private NqtDichVu nqtDichVu;
}

