package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NqtPhongRepository extends JpaRepository<NqtPhong, Integer> {
    Optional<NqtPhong> findByNqtSoPhong(String nqtSoPhong);
    boolean existsByNqtSoPhong(String nqtSoPhong);
}

