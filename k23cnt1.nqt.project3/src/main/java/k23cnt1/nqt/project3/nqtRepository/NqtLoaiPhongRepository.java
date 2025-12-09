package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtLoaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NqtLoaiPhongRepository extends JpaRepository<NqtLoaiPhong, Integer> {
    List<NqtLoaiPhong> findByNqtStatus(Boolean nqtStatus);
}
