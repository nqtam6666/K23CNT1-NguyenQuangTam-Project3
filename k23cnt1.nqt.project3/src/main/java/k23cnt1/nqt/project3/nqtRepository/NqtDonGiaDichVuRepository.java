package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtDonGiaDichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NqtDonGiaDichVuRepository extends JpaRepository<NqtDonGiaDichVu, Integer> {
}

