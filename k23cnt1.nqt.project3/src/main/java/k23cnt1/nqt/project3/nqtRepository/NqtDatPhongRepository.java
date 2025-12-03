package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NqtDatPhongRepository extends JpaRepository<NqtDatPhong, Integer> {

    @org.springframework.data.jpa.repository.Query("SELECT SUM(d.nqtTongTien) FROM NqtDatPhong d WHERE d.nqtStatus = 1")
    Double sumTongTienDaThanhToan();

    @org.springframework.data.jpa.repository.Query("SELECT MONTH(d.nqtNgayDen), SUM(d.nqtTongTien) FROM NqtDatPhong d WHERE d.nqtStatus = 1 GROUP BY MONTH(d.nqtNgayDen)")
    java.util.List<Object[]> sumTongTienThang();
}
