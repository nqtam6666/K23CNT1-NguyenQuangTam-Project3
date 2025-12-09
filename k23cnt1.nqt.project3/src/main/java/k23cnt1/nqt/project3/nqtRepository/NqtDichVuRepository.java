package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtDichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NqtDichVuRepository extends JpaRepository<NqtDichVu, Integer> {
    List<NqtDichVu> findByNqtStatus(Boolean nqtStatus);
}
