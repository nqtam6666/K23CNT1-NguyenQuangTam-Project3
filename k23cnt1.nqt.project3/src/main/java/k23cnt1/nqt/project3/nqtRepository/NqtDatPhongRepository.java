package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NqtDatPhongRepository extends JpaRepository<NqtDatPhong, Integer> {
}

