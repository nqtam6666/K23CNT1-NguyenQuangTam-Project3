package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NqtNguoiDungRepository extends JpaRepository<NqtNguoiDung, Integer> {
    Optional<NqtNguoiDung> findByNqtTaiKhoan(String nqtTaiKhoan);

    Optional<NqtNguoiDung> findByNqtTaiKhoanOrNqtEmail(String nqtTaiKhoan, String nqtEmail);

    boolean existsByNqtTaiKhoan(String nqtTaiKhoan);
}
