package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtDanhGia;
import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NqtDanhGiaRepository extends JpaRepository<NqtDanhGia, Integer> {
    List<NqtDanhGia> findByNqtDatPhong(NqtDatPhong nqtDatPhong);
    
    @Query("SELECT d FROM NqtDanhGia d WHERE d.nqtStatus = :status ORDER BY d.nqtNgayDanhGia DESC")
    List<NqtDanhGia> findByNqtStatusOrderByNqtNgayDanhGiaDesc(@Param("status") Boolean nqtStatus);
    
    @Query("SELECT d FROM NqtDanhGia d " +
           "LEFT JOIN FETCH d.nqtDatPhong dp " +
           "LEFT JOIN FETCH dp.nqtNguoiDung " +
           "LEFT JOIN FETCH dp.nqtPhong " +
           "WHERE d.nqtStatus = :status ORDER BY d.nqtNgayDanhGia DESC")
    List<NqtDanhGia> findByNqtStatusOrderByNqtNgayDanhGiaDescWithFetch(@Param("status") Boolean nqtStatus);
    
    List<NqtDanhGia> findByNqtDatPhongAndNqtStatus(NqtDatPhong nqtDatPhong, Boolean nqtStatus);
}

