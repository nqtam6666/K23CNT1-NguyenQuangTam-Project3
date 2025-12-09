package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NqtSettingRepository extends JpaRepository<NqtSetting, Integer> {
    Optional<NqtSetting> findByNqtName(String nqtName);
}
