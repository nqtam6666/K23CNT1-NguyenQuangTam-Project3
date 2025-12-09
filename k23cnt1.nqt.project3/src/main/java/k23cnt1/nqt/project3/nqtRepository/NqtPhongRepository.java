package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtLoaiPhong;
import k23cnt1.nqt.project3.nqtEntity.NqtPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NqtPhongRepository extends JpaRepository<NqtPhong, Integer> {
    Optional<NqtPhong> findByNqtSoPhong(String nqtSoPhong);

    boolean existsByNqtSoPhong(String nqtSoPhong);

    List<NqtPhong> findByNqtStatus(Boolean nqtStatus);

    List<NqtPhong> findByNqtStatusOrderByNqtIdDesc(Boolean nqtStatus);

    List<NqtPhong> findByNqtLoaiPhongAndNqtStatus(NqtLoaiPhong nqtLoaiPhong, Boolean nqtStatus);

    List<NqtPhong> findByNqtTenPhongContainingOrNqtSoPhongContaining(String nqtTenPhong, String nqtSoPhong);
}
