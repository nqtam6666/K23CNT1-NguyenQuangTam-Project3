package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtDanhGia;
import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NqtDanhGiaRepository extends JpaRepository<NqtDanhGia, Integer> {
    List<NqtDanhGia> findByNqtDatPhong(NqtDatPhong nqtDatPhong);
    List<NqtDanhGia> findByNqtStatusOrderByNqtNgayDanhGiaDesc(Boolean nqtStatus);
    List<NqtDanhGia> findByNqtDatPhongAndNqtStatus(NqtDatPhong nqtDatPhong, Boolean nqtStatus);
}

